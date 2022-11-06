package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.commons.bean.BizCode;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.OmsFeignService;
import com.atguigu.gulimall.order.feign.UmsFeignService;
import com.atguigu.gulimall.order.feign.WmsFeignService;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.*;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.order.OrderFeignSubmitVo;
import com.atguigu.gulimall.order.vo.ware.LockStockVo;
import com.atguigu.gulimall.order.vo.ware.SkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    //  ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    @Autowired
    @Qualifier("mainThreadPool")
    ThreadPoolExecutor poolExecutor;


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UmsFeignService umsFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    WmsFeignService wmsFeignService;

    @Override
    public Order createOrder() {
        Order order = new Order();
        order.setOrderId(IdWorker.getId());
        order.setDesc("商品xxxxds");
        order.setStatus(0);

        //订单创建完成就给MQ发送一条消息
        rabbitTemplate.convertAndSend("CreateOrderEX", "create.order", order);


        //利用定时线程池
        //有啥问题？
//        executorService.schedule(()->{
//            System.out.println(order+"已经过期，正准备查询数据库，决定是否关单");
//        },30, TimeUnit.SECONDS);


//        executorService.scheduleAtFixedRate()
        return order;


    }

    @Override
    public OrderConfirmVo selectConfirms(String authorization) {

        Map<String, Object> body = GuliJwtUtils.getJwtBody(authorization);
        Long id = Long.parseLong(String.valueOf(body.get("id")));
//      String token = (String.valueOf(body.get("token")));

        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();

        //调用ums 查询地址
        CompletableFuture<Void> firstTask = CompletableFuture.runAsync(() -> {
            Resp<List<MemberAddressVo>> memberAddress = umsFeignService.memberAddress(id);
            List<MemberAddressVo> data = memberAddress.getData();
            if (data != null && data.size() > 0) {
                orderConfirmVo.setAddresses(data);
            }
        }, poolExecutor);

        CompletableFuture<Void> secondTask = CompletableFuture.runAsync(() -> {
            Resp<CartVo> cartVoResp = cartFeignService.selectCartWithStatus(id);
            CartVo cartVo = cartVoResp.getData();
            if (cartVo != null) {
                orderConfirmVo.setCartVo(cartVo);
            }
        }, poolExecutor);

        CompletableFuture<Void> finalWaitTask = CompletableFuture.allOf(firstTask, secondTask);

        String uuId = IdWorker.getTimeId();

        redisTemplate.opsForValue().set(Constant.TOKENS + uuId, uuId, Constant.TOKENS_TIME_OUT, TimeUnit.SECONDS);

        orderConfirmVo.setOrderToken(uuId);

        try {
            finalWaitTask.get();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } catch (ExecutionException e) {
            log.error(e.getMessage());
        }

        //调用get cart


        return orderConfirmVo;
    }

    @Autowired
    JedisPool jedisPool;

    @Autowired
    OmsFeignService omsFeignService;

    @Override
    public Resp<Object> submit(OrderSubmitVo vo, String authorization) {
        Long currentUserId = getCurrentUserId(authorization);
        //valid token
        String Token = redisTemplate.opsForValue().get(Constant.TOKENS + vo.getOrderToken());

        //delete token
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                "then return redis.call('del',KEYS[1]) else return 0 end";
        Jedis jedis = jedisPool.getResource();
        Long eval = (Long) jedis.eval(script, Arrays.asList(Constant.TOKENS + vo.getOrderToken()), Arrays.asList(vo.getOrderToken()));

        try {
            if (eval == 1) {
                // 有token delete
                //valid stock/lock stock
                //1.1111111111111111111111 derive all chosen items info
                Resp<CartVo> cartVoResp = cartFeignService.selectCartWithStatus(currentUserId);
                CartVo cartVo = cartVoResp.getData();
                List<CartItemVo> items = cartVo.getItems();

                List<SkuLockVo> skuIds = new ArrayList<>();

                if (items != null && items.size() > 0) {
                    for (CartItemVo item : items) {
                        SkuLockVo skuLockVo = new SkuLockVo();
                        skuLockVo.setNum(item.getNum());
                        skuLockVo.setSkuId(item.getSkuId());
                        skuLockVo.setOrderToken(Token);
                        skuIds.add(skuLockVo);
                    }
                }


                //1111111111111lock stock
                Resp<LockStockVo> lockStockVoResp = null;
                try {
                    //mq skus
                    lockStockVoResp = wmsFeignService.lockAndCheckStock(skuIds);
                } catch (Exception e) {
                    Resp<Object> fail = Resp.fail(null);
                    fail.setCode(BizCode.SERVICE_UNAVAILABLE.getCode());
                    fail.setMsg(BizCode.SERVICE_UNAVAILABLE.getMsg());
                    return fail;
                }



                if (lockStockVoResp.getData().getLocked()) {
                    //lock stock then
                    //222222222222222222222valid price
//                    BigDecimal totalPrice = vo.getTotalPrice();
//                    Resp<CartVo> cartVoPrice = cartFeignService.selectCartWithStatus(currentUserId);
//                    int i = cartVoPrice.getData().getTotalPrice().compareTo(totalPrice);
//                    if (i != 0) {
//                        Resp<Object> fail = Resp.fail(null);
//                        fail.setCode(BizCode.ORDER_NEED_REFRESH.getCode());
//                        fail.setMsg(BizCode.ORDER_NEED_REFRESH.getMsg());
//                        return fail;
//                    }
//
                    //3333333333333333333create an order
                    OrderFeignSubmitVo orderFeignSubmitVo = new OrderFeignSubmitVo();
                    BeanUtils.copyProperties(vo, orderFeignSubmitVo);
                    Long addressId = vo.getAddressId();
                    Resp<MemberAddressVo> info = umsFeignService.info(addressId);
                    MemberAddressVo addressVo = info.getData();

                    orderFeignSubmitVo.setReceiverName(addressVo.getName());
                    orderFeignSubmitVo.setReceiverDetailAddress(addressVo.getDetailAddress());
                    orderFeignSubmitVo.setReceiverPhone(addressVo.getPhone());
                    orderFeignSubmitVo.setOrderToken(vo.getOrderToken());
                    orderFeignSubmitVo.setCartVo(cartVo);
                    Resp<OrderEntityVo> andSaveOrder = null;
                    try {
                        andSaveOrder = omsFeignService.createAndSaveOrder(orderFeignSubmitVo);
                    } catch (Exception e) {
                        //如果这里出异常 就把库存给加回去

                        Resp<Object> fail = Resp.fail(null);
                        fail.setCode(BizCode.SERVICE_UNAVAILABLE.getCode());
                        fail.setMsg(BizCode.SERVICE_UNAVAILABLE.getMsg());
                        return fail;
                    }
                    return Resp.ok(andSaveOrder);
// TODO: 2022/11/3
//                    //44444444444444444444444remove chosen items in redis
//                    CartVo cartVo1data = cartVoResp.getData();
//                    List<CartItemVo> items1 = cartVo1data.getItems();
//                    ArrayList<Long> longs = new ArrayList<>();
//                    for (CartItemVo cartItemVo : items1) {
//                        longs.add(cartItemVo.getSkuId());
//                    }
//                    ClearCartSkuVo clearCartSkuVo = new ClearCartSkuVo();
//                    clearCartSkuVo.setSkuIds(longs);
//                    clearCartSkuVo.setUserId(currentUserId);
//                    //                 cartFeignService.clearSkuIds(clearCartSkuVo);
//
//
//                    //reduce stock after payment
//
//
//                    return Resp.ok(andSaveOrder);
//
//
//                } else {
//                    LockStockVo data = lockStockVoResp.getData();
//                    List<SkuLock> unLocks = data.getLocks();
//
//                    ArrayList<SkuLock> returnVos = new ArrayList<>();
//                    for (SkuLock unLock : unLocks) {
//                        if (unLock.getSuccess()) {
//                            continue;
//                        }
//                        returnVos.add(unLock);
//                    }
//
//
//                    Resp<Object> fail = Resp.fail(returnVos);
//                    fail.setCode(BizCode.STOCK_NOT_ENOUGH.getCode());
//                    fail.setMsg(BizCode.STOCK_NOT_ENOUGH.getMsg());
//                    return fail;
                }


            } else {
                // validation failure
//            throw new TokenNotFoundException("Token Not Found");
                Resp<Object> fail = Resp.fail(null);
                fail.setCode(BizCode.TOKEN_INVAILIED.getCode());
                fail.setMsg(BizCode.TOKEN_INVAILIED.getMsg());
                return fail;
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }

  return Resp.ok(null);
    }

    @Override
    public void paySuccess(String orderNum) {
        omsFeignService.paySuccess(orderNum);
    }

    @Override
    public MiaoShaVo miaoSha(Long skuId, Long id) {
        RSemaphore semaphore = redissonClient.getSemaphore("skuId-28");
        boolean b = semaphore.tryAcquire(1);

        if(!b){
            return null;
        }

        MiaoShaVo miaoShaVo = new MiaoShaVo();
        miaoShaVo.setToken(IdWorker.getTimeId());
        miaoShaVo.setSkuId(skuId);
        miaoShaVo.setUserId(id);
        rabbitTemplate.convertAndSend("msExchange","msKey",miaoShaVo);

        return miaoShaVo;
    }

    private Long getCurrentUserId(String authorization) {
        Object id = GuliJwtUtils.getJwtBody(authorization).get("id");
        return Long.parseLong(id.toString());
    }

}
