package com.atguigu.gulimall.oms.service.impl;

import com.atguigu.gulimall.oms.dao.OrderItemDao;
import com.atguigu.gulimall.oms.entity.OrderItemEntity;
import com.atguigu.gulimall.oms.vo.CartItemVo;
import com.atguigu.gulimall.oms.vo.CartVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.oms.dao.OrderDao;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.service.OrderService;
import org.springframework.transaction.annotation.Transactional;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemDao orderItemDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }
    @Transactional
    @Override
    public OrderEntity createAndSaveOrder(OrderSubmitVo orderSubmitVo) {
        CartVo cartVo = orderSubmitVo.getCartVo();
        List<CartItemVo> items = cartVo.getItems();
        OrderEntity orderEntity = new OrderEntity();

        //order num
        orderEntity.setOrderSn(orderSubmitVo.getOrderToken());

        orderEntity.setMemberId(orderSubmitVo.getUserId());

        orderEntity.setMemberUsername(orderSubmitVo.getReceiverName());

        orderEntity.setTotalAmount(cartVo.getTotalPrice());

        orderEntity.setPayAmount(cartVo.getCartPrice());

        orderEntity.setMemberId(orderSubmitVo.getUserId());

        orderEntity.setPromotionAmount(cartVo.getReductionPrice());

        orderEntity.setNote(orderSubmitVo.getRemark());

        BeanUtils.copyProperties(orderSubmitVo, orderEntity);

        orderDao.insert(orderEntity);

        Long orderId = orderEntity.getId();

        //ORDER ITEMS
        for (CartItemVo item : items) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderId(orderId);
            orderItemEntity.setOrderSn(orderEntity.getOrderSn());
            orderItemEntity.setSkuId(item.getSkuId());
            orderItemEntity.setSkuName(item.getSkuTitle());
            orderItemEntity.setSkuPrice(item.getPrice());
            orderItemEntity.setSkuQuantity(item.getNum());

            orderItemDao.insert(orderItemEntity);

        }


        return orderEntity;
    }

    @Override
    public OrderEntity findByOrderNum(String orderToken) {
        OrderEntity orderEntity = orderDao.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));

        return orderEntity;
    }

}