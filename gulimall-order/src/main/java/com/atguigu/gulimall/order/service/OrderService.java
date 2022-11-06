package com.atguigu.gulimall.order.service;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.MiaoShaVo;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;

public interface OrderService {
    Order createOrder();

    OrderConfirmVo selectConfirms(String authorization);

    Resp<Object> submit(OrderSubmitVo vo, String authorization);

    void paySuccess(String orderNum);

    MiaoShaVo miaoSha(Long id, Long skuId);
}
