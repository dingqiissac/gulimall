package com.atguigu.gulimall.oms.service.impl;

import com.atguigu.gulimall.oms.dao.OrderDao;
import com.atguigu.gulimall.oms.dao.OrderItemDao;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

@Service
public class RabbitReceiverOrderDeleteImpl {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemDao orderItemDao;


    @Transactional
    @RabbitListener(queues = {"deleteOrderQueue"})
    public void deleteOrder(Message message, Channel channel, HashMap<String, String> orderNum) throws IOException {

        try {
            //有订单就删除
            String orderToken = orderNum.get("orderToken");

            orderDao.delete(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));

            //有详细订单项就删除
            orderItemDao.delete(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderToken));


            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            throw new RuntimeException("没删除成功，发送ack也有问题");
        }


    }


}
