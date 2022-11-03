package com.atguigu.gulimall.order.controller;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVo vo,
                               @RequestHeader(name = "Authorization",required = false) String Authorization){

        Resp<Object> submit = orderService.submit(vo, Authorization);
        Object data = submit.getData();
        if(data instanceof OrderEntityVo){
          //produce a payment page
        }

        return submit;
    }


}
