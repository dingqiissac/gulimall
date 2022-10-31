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

    @Bean("CreateOrderEX")
    public Exchange orderCreateExchange(){

        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        return new DirectExchange("CreateOrderEX",true,false,null);
    }

    @Bean("CreateOrderQu")  //死信队列，千万不要有人消费
    public Queue deadOrderStorageQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object>
         */
        Map<String,Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange","DeadExchange"); //信死了以后发给那个交换机，而不是丢弃
        properties.put("x-dead-letter-routing-key","dead.order");
        properties.put("x-message-ttl",1000*30);//ms为单位
        return new Queue("CreateOrderQu",true,false,false,properties);
    }

    @Bean("CreateOrderBinding")
    public Binding deadOrderRoutingBinding(){
        /**
         * String destination,
         * DestinationType destinationType,
         * String exchange,
         * String routingKey,
         * Map<String, Object> arguments
         */
        return new Binding("CreateOrderQu",
                Binding.DestinationType.QUEUE,
                "CreateOrderEX",
                "create.order",null);
    }

    //============以上订单创建的信息能保存到死信队列里面==============
    @Bean("DeadExchange")
    public Exchange orderDeadExchange(){

        return new DirectExchange("DeadExchange",true,false,null);
    }

    @Bean("deadOrderQueue")
    public Queue deadOrderQueue(){
        return new Queue("deadOrderQueue",true,false,false,null);
    }

    @Bean("deadBinding")
    public Binding deadBinding(){
        return new Binding("deadOrderQueue",
                Binding.DestinationType.QUEUE,
                "DeadExchange",
                "dead.order",
                null);
    }




}
