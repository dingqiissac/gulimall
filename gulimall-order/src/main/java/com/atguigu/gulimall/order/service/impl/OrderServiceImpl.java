package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.Order;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public Order createOrder() {
        Order order = new Order();
        order.setOrderId(IdWorker.getId());
        order.setDesc("商品xxxxds");
        order.setStatus(0);

        //订单创建完成就给MQ发送一条消息
//        rabbitTemplate.convertAndSend("orderCreateExchange","create.order",order);


        //利用定时线程池
        //有啥问题？
        executorService.schedule(()->{
            System.out.println(order+"已经过期，正准备查询数据库，决定是否关单");
        },30, TimeUnit.SECONDS);


//        executorService.scheduleAtFixedRate()
        return order;



    }


    //@RabbitListener(queues = "closeOrderQueue")
    public void closeOrder(Order order, Channel channel, Message message) throws IOException {

        System.out.println("收到的订单内容："+order);

        Long orderId = order.getOrderId();
        System.out.println("正在数据库查询【"+orderId+"】订单状态，"+order.getStatus());

        if(order.getStatus() != 1){
            System.out.println("这个订单没有被支付，正在准备关闭。。。数据库状态改为-1");
        }


        //给MQ回复，我们已经处理完成此消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
