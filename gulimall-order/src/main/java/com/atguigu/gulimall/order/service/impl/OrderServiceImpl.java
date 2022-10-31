package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.UmsFeignService;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.CartVo;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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


    //@RabbitListener(queues = "closeOrderQueue")
    public void closeOrder(Order order, Channel channel, Message message) throws IOException {

        System.out.println("收到的订单内容：" + order);

        Long orderId = order.getOrderId();
        System.out.println("正在数据库查询【" + orderId + "】订单状态，" + order.getStatus());

        if (order.getStatus() != 1) {
            System.out.println("这个订单没有被支付，正在准备关闭。。。数据库状态改为-1");
        }


        //给MQ回复，我们已经处理完成此消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }
}
