package com.atguigu.gulimall.wms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.wms.config.Constant;
import com.atguigu.gulimall.wms.dao.WareSkuDao;
import com.atguigu.gulimall.wms.feign.OmsFeignService;
import com.atguigu.gulimall.wms.service.WareReleaseService;
import com.atguigu.gulimall.wms.vo.OrderVo;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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


    //   监听队列过时的锁库信息
    @Transactional
    @RabbitListener(queues = Constant.SKU_ID_DEAD_QUEUE)
    public void release(Message message, Channel channel, List<SkuLock> skuLocks) throws IOException {
        try {
            if (skuLocks != null && skuLocks.size() > 0) {
                String orderToken = skuLocks.get(0).getOrderToken();
                Resp<OrderVo> byOrderNum = omsFeignService.findByOrderNum(orderToken);
                OrderVo data = byOrderNum.getData();

                //判断是否有订单 或是状态为0 then rollback stock
                if (data == null || (data != null && data.getStatus() == 0)) {
                    for (SkuLock skuLock : skuLocks) {
                        if (skuLock.getSuccess()) {
                            wareSkuDao.updateRollBackStock(skuLock);
                        }
                    }
                    //send message to orderDeleteQueue
                    HashMap<String, String> res = new HashMap<>();
                    res.put("orderToken",orderToken);
                    rabbitTemplate.convertAndSend(Constant.SKU_ID_EXCHANGE, Constant.ORDER_DELETE_QUEUE_ROUTE_KEY, res);
                }

            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e.getMessage());
        }


    }

}
