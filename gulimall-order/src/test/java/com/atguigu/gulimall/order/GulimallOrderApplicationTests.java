package com.atguigu.gulimall.order;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;


    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 1、发消息要将消息序列化发送出去
     * 2、收消息要将消息反序列化进来
     * 3、我们给消息队列发的对象要实现序列化接口；
     *
     * 4、希望消息都是json
     *    1）、没有回复消息【我收到了】，但是新的消息也能收到
     *    2）、所有消息都是Unacked状态；
     *
     *
     */
    @Test
    public void sendMsg(){

        Map<String,Object> map = new HashMap<>();

//       rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        for (int i = 0; i < 10; i++) {
            map.put("username", UUID.randomUUID());
            map.put("age",i);
            rabbitTemplate.convertAndSend("myExchange-Ding","djkjs", map);
        }



        System.out.println("消息发送完成...");
    }




    /**
     * 1、创建队列
     */
    @Test
    public void contextLoads() {

        /**
         * String name, 队列的名字
         * boolean durable, 是否持久化
         * boolean exclusive, 排他，只能有一个人连接它
         * boolean autoDelete, 自动删除
         * Map<String, Object> arguments 参数
         */
        Queue queue = new Queue("hello-queue",true,false,false,null);

        //创建一个队列
        String declareQueue = amqpAdmin.declareQueue(queue);
        System.out.println("队列创建完成...."+declareQueue);
    }

    @Test
    public void testExchange(){
        //Exchange exchange

        /**
         * String name,   交换机名字
         * boolean durable, 是否持久化
         * boolean autoDelete, 是否自动删除
         * Map<String, Object> arguments  交换机的设置
         */
        DirectExchange directExchange = new DirectExchange("hello-lfy-exchange",true,false,null);
        amqpAdmin.declareExchange(directExchange);
        System.out.println("交换机创建完成....");

    }

    @Test
    public void testcreateExchangeQueueBinding(){

        /**
         * String destination,：目的地
         * DestinationType destinationType,：目的地类型
         * String exchange,：交换机
         * String routingKey,：路由键
         * Map<String, Object> arguments：参数
         *
         * 将 【交换机】 绑定 到 【目的地】 目的地的类型是【destinationType】，使用【路由键】
          */
        Binding binding = new Binding("hello-queue",
                Binding.DestinationType.QUEUE,
                "hello-lfy-exchange",
                "hello.world",
                null);
        amqpAdmin.declareBinding(binding);
        System.out.println("绑定关系创建完成....");
    }

}
