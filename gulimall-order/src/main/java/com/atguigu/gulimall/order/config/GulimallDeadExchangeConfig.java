package com.atguigu.gulimall.order.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置一组能完成  【死信路由+延迟队列】 功能
 */
@EnableRabbit
@Configuration
public class GulimallDeadExchangeConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @Bean("msExchange")
    public Exchange orderCreateExchange(){

        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        return new DirectExchange("msExchange",true,false,null);
    }

    @Bean("msQueue")
    public Queue msQueue(){
        return new Queue("msQueue",true,false,false,null);
    }


    @Bean("msDelayedQueue")  //死信队列，千万不要有人消费
    public Queue msDelayedQueue(){
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object>
         */
        Map<String,Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange","msExchange"); //信死了以后发给那个交换机，而不是丢弃
        properties.put("x-dead-letter-routing-key","msDeadKey");
        properties.put("x-message-ttl",1000*100);//ms为单位
        return new Queue("msDelayedQueue",true,false,false,properties);
    }

    @Bean("msDeadQueue")
    public Queue msDeadQueue(){
        return new Queue("msDeadQueue",true,false,false,null);
    }

    @Bean("first")
    public Binding first(){

        return new Binding("msQueue",
                Binding.DestinationType.QUEUE,
                "msExchange",
                "msKey",null);
    }

    @Bean("second")
    public Binding second(){

        return new Binding("msDelayedQueue",
                Binding.DestinationType.QUEUE,
                "msExchange",
                "msDelayedKey",null);
    }

    @Bean("third")
    public Binding third(){

        return new Binding("msDeadQueue",
                Binding.DestinationType.QUEUE,
                "msExchange",
                "msDeadKey",null);
    }


}
