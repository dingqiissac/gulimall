package com.atguigu.gulimall.order.controller;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    OrderService orderService;
//    /**
//     * 创建订单
//     * @return
//     */
//    @GetMapping("/create")
//    public Order createOrder(){
//
//       Order order =  orderService.createOrder();
//
//       return order;
//    }
    @GetMapping("/confirm")
    public Resp<OrderConfirmVo> orderConfirm(@RequestHeader(name = "Authorization",required = false) String Authorization){

        OrderConfirmVo res = orderService.selectConfirms(Authorization);

        return Resp.ok(res);
    }



}
