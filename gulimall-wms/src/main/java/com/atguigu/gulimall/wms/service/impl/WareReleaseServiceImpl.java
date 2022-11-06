package com.atguigu.gulimall.wms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.wms.config.Constant;
import com.atguigu.gulimall.wms.dao.WareSkuDao;
import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.feign.OmsFeignService;
import com.atguigu.gulimall.wms.service.WareReleaseService;
import com.atguigu.gulimall.wms.vo.MiaoShaVo;
import com.atguigu.gulimall.wms.vo.OrderVo;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import com.rabbitmq.client.Channel;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class WareReleaseServiceImpl implements WareReleaseService {
    @Autowired
    OmsFeignService omsFeignService;

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RedisTemplate redisTemplate;


    //查看秒杀订单
    @Transactional
    @RabbitListener(queues = "msDeadQueue")
    public void checkOrder(Message message, Channel channel, MiaoShaVo miaoShaVo) throws IOException {
       try {
           Resp<OrderVo> byOrderNum = omsFeignService.findByOrderNum(miaoShaVo.getToken());
           OrderVo data = byOrderNum.getData();

           if (data == null || (data != null && data.getStatus() == 0)) {
               //未付款 就删订单 订单项 redisson +1
               omsFeignService.deleteOrderAndItems(miaoShaVo.getToken());
               RSemaphore semaphore = redissonClient.getSemaphore("skuId-28");
               semaphore.release();
           } else if (data != null && data.getStatus() == 1) {
               //减掉库存
               SkuLockVo skuLockVo = new SkuLockVo();
               skuLockVo.setNum(1);
               skuLockVo.setSkuId(miaoShaVo.getSkuId());
               List<WareSkuEntity> wareSkuEntities = wareSkuDao.checkStock(skuLockVo);
               WareSkuEntity wareSkuEntity = wareSkuEntities.get(0);

               SkuLock skuLock = new SkuLock();
               skuLock.setSkuId(miaoShaVo.getSkuId());
               skuLock.setLocked(1);
               skuLock.setWareId(wareSkuEntity.getWareId());

               wareSkuDao.reduceStocks(skuLock);
           }

           channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
       }catch (Exception e){
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
       }


    }


    //监听队列过时的锁库信息
//    @Transactional
//    @RabbitListener(queues = Constant.SKU_ID_DEAD_QUEUE)
//    public void release(Message message, Channel channel, List<SkuLock> skuLocks) throws IOException {
//        try {
//            if (skuLocks != null && skuLocks.size() > 0) {
//                String orderToken = skuLocks.get(0).getOrderToken();
//                Resp<OrderVo> byOrderNum = omsFeignService.findByOrderNum(orderToken);
//                OrderVo data = byOrderNum.getData();
//
//                //判断是否有订单 或是状态为0 then rollback stock
//                if (data == null || (data != null && data.getStatus() == 0)) {
//                    for (SkuLock skuLock : skuLocks) {
//                        if (skuLock.getSuccess()) {
//                            wareSkuDao.updateRollBackStock(skuLock);
//                        }
//                    }
//                    //send message to orderDeleteQueue
//                    HashMap<String, String> res = new HashMap<>();
//                    res.put("orderToken", orderToken);
//                    rabbitTemplate.convertAndSend(Constant.SKU_ID_EXCHANGE, Constant.ORDER_DELETE_QUEUE_ROUTE_KEY, res);
//                } else if ((data != null && data.getStatus() == 1)) {
//                    //reduce stock
//                    for (SkuLock skuLock : skuLocks) {
//                        if (skuLock.getSuccess()) {
//                            wareSkuDao.reduceStocks(skuLock);
//                        }
//                    }
//                }
//
//            }
//
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//
//        } catch (Exception e) {
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//            throw new RuntimeException(e.getMessage());
//        }
//
//
//    }

}
