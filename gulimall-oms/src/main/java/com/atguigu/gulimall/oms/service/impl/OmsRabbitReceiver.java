package com.atguigu.gulimall.oms.service.impl;

import com.atguigu.gulimall.oms.dao.OrderDao;
import com.atguigu.gulimall.oms.dao.OrderItemDao;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.entity.OrderItemEntity;
import com.atguigu.gulimall.oms.vo.MiaoShaVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;


@Service
public class OmsRabbitReceiver {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = "msQueue")
    public void produceOrder(Message message, Channel channel, MiaoShaVo miaoShaVo) throws IOException {
        try {

            //business produce Order
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderSn(miaoShaVo.getToken());
            orderEntity.setMemberId(miaoShaVo.getUserId());
            orderEntity.setPayAmount(new BigDecimal("100"));
            orderEntity.setTotalAmount(new BigDecimal("100"));
            orderEntity.setCouponId(1l);
            orderEntity.setBillType(1);
            orderDao.insert(orderEntity);


            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderSn(miaoShaVo.getToken());
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setSkuId(miaoShaVo.getSkuId());
            orderItemEntity.setRealAmount(new BigDecimal("100"));
            orderItemDao.insert(orderItemEntity);

            rabbitTemplate.convertAndSend("msExchange","msDelayedKey",miaoShaVo);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

}
