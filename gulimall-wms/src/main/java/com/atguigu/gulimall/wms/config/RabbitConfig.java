package com.atguigu.gulimall.wms.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@EnableRabbit
@Configuration
public class RabbitConfig {


    @Bean
    public  MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }



    @Bean(Constant.SKU_ID_EXCHANGE)
    public Exchange skuIdExchange(){
        return new TopicExchange(Constant.SKU_ID_EXCHANGE,true,false,null);
    }

    @Bean(Constant.SKU_ID_DELAYED_QUEUE)  //死信队列，千万不要有人消费
    public Queue skuIdDelayedQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object>
         */
        Map<String,Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange",Constant.SKU_ID_EXCHANGE); //信死了以后发给那个交换机，而不是丢弃
        properties.put("x-dead-letter-routing-key",Constant.TIME_OUT_MESSAGE_ROUTE_KEY);
        properties.put("x-message-ttl",1000*100);//ms为单位
        return new Queue(Constant.SKU_ID_DELAYED_QUEUE,true,false,false,properties);
    }

    @Bean(Constant.SKU_ID_DEAD_QUEUE)  //死信队列，千万不要有人消费  monitor this queue
    public Queue deadskuStockStorageQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object>
         */

        return new Queue(Constant.SKU_ID_DEAD_QUEUE,true,false,false,null);
    }


    //queue for order service
    @Bean(Constant.ORDER_DELETE_QUEUE)
    public Queue deleteOrderQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object>
         */

        return new Queue(Constant.ORDER_DELETE_QUEUE,true,false,false,null);
    }





    @Bean("firstBinding")
    public Binding firstBinding(){
        return new Binding(Constant.SKU_ID_DELAYED_QUEUE, Binding.DestinationType.QUEUE,
                Constant.SKU_ID_EXCHANGE,Constant.TO_DELAYED_QUEUE_ROUTE_KEY,null);
    }

    @Bean("secondBinding")
    public Binding secondBinding(){
        return new Binding(Constant.SKU_ID_DEAD_QUEUE, Binding.DestinationType.QUEUE,
                Constant.SKU_ID_EXCHANGE,Constant.TIME_OUT_MESSAGE_ROUTE_KEY,null);
    }

    @Bean("thirdBinding")
    public Binding thirdBinding(){
        return new Binding(Constant.ORDER_DELETE_QUEUE, Binding.DestinationType.QUEUE,
                Constant.SKU_ID_EXCHANGE,Constant.ORDER_DELETE_QUEUE_ROUTE_KEY,null);
    }



}
