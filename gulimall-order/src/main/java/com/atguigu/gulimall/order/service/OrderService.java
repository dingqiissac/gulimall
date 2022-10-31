package com.atguigu.gulimall.order.service;

import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;

public interface OrderService {
    Order createOrder();

    OrderConfirmVo selectConfirms(String authorization);
}
