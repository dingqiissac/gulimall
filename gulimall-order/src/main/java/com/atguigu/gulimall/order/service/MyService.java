package com.atguigu.gulimall.order.service;


import com.atguigu.gulimall.order.vo.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class MyService {


    /**
     * 1、多个人监听同一个队列，谁收到消息？
     *      只有一个人能收到消息。（消息重复消费）
     *
     * 2、service监听消息队列里面的内容，方法参数能写哪些？
     *      Map<String,Object> content：自定义对象；将队列的内容自动的转为这个对象
     *      Message message：可以获取到当前消息的详细信息；（消息的id，什么时候发送的....）
     *      Channel channel：通道，可以ack数据
     *      参数不分先后顺序，不一定全要或者要哪些
     *
     *  3、切换到手动ack模式
     *
     *
     *  4、我们如果没有回复消息；
     *      1）、我们一直在线：此消息不会被再发给别人；
     *      2）、一旦掉线，未回复的消息又会变成ready；又可以发给别人
     *
     *
     *
     * @param order
     */
    @RabbitListener(queues = "deadOrderQueue")
    public void hello(Message message, Order order,Channel channel) throws IOException {
        System.out.println("order..."+order);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

        /**
         * long deliveryTag,消息的标签
         * boolean multiple,是否批量拒绝多个消息
         * boolean requeue,是否重新将消息放回队列,false【discarded【丢弃】/dead-lettered[死信]】
         */
        //1、拒绝(unack)不入队，消息直接被丢弃
        //2、拒绝(unacke)入队。消息会被立即重新投递。
        //3、以前的不回复。消息是unacked状态，如果consumer断了。unacked变成ready成能下一次重新投递
//        channel.basicNack(message.getMessageProperties().getDeliveryTag(),
//                false,
//                true);

        //4、拒绝(reject)不入队，和1一样
        //5、拒绝(reject)入队。消息会被立即重新投递。
       // channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        //6、消息会被移除
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

        //没响应的回复
        //我不要的回复

    }

//    @RabbitListener(queues = "myqueue")
//    public void hello2(Map<String,Object> content,Channel channel){
//        System.out.println("hello2----"+content);
//
//        //channel.basicNack();
//    }

//    @RabbitListener(queues = "myQueue-Ding")
//    public void hello1(Message message,Map<String,Object> content, Channel channel) throws IOException {
//        System.out.println("hello1..." + content);
//    }
}
