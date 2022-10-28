package com.atguigu.gulimall.order.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置一组能完成  【死信路由+延迟队列】 功能
 */
@Configuration
public class GulimallDeadExchangeConfig {

    @Bean("orderCreateExchange")
    public Exchange orderCreateExchange(){

        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        return new DirectExchange("orderCreateExchange",true,false,null);
    }

    @Bean("deadOrderStorageQueue")  //死信队列，千万不要有人消费
    public Queue deadOrderStorageQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object>
         */
        Map<String,Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange","orderDeadExchange"); //信死了以后发给那个交换机，而不是丢弃
        properties.put("x-dead-letter-routing-key","dead.order");
        properties.put("x-message-ttl",1000*30);//ms为单位
        return new Queue("deadOrderStorageQueue",true,false,false,properties);
    }

    @Bean("deadOrderRoutingBinding")
    public Binding deadOrderRoutingBinding(){
        /**
         * String destination,
         * DestinationType destinationType,
         * String exchange,
         * String routingKey,
         * Map<String, Object> arguments
         */
        return new Binding("deadOrderStorageQueue",
                Binding.DestinationType.QUEUE,
                "orderCreateExchange",
                "create.order",null);
    }

    //============以上订单创建的信息能保存到死信队列里面==============
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){

        return new DirectExchange("orderDeadExchange",true,false,null);
    }

    @Bean("closeOrderQueue")
    public Queue deadOrderQueue(){
        return new Queue("closeOrderQueue",true,false,false,null);
    }

    @Bean("deadBinding")
    public Binding deadBinding(){
        return new Binding("closeOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderDeadExchange",
                "dead.order",
                null);
    }




}
