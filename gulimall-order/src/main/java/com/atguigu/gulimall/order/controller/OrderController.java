package com.atguigu.gulimall.order.controller;


import com.alipay.api.AlipayApiException;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.MiaoShaVo;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.pay.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    @PostMapping("/miaosha")
    public Resp<MiaoShaVo> miaoSha(@RequestParam("skuId") Long skuId,
                                   @RequestHeader(name = "Authorization", required = false) String Authorization) {
        Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(Authorization);
        Object id = jwtBody.get("id");
        long userId = Long.parseLong(id.toString());

        MiaoShaVo res = orderService.miaoSha(skuId,userId);

        return Resp.ok(res);
    }



    @GetMapping("/confirm")
    public Resp<OrderConfirmVo> orderConfirm(@RequestHeader(name = "Authorization", required = false) String Authorization) {

        OrderConfirmVo res = orderService.selectConfirms(Authorization);

        return Resp.ok(res);
    }

    @PostMapping("/submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVo vo,
                               @RequestHeader(name = "Authorization", required = false) String Authorization) throws UnsupportedEncodingException, AlipayApiException {

        Resp<Object> submit = orderService.submit(vo, Authorization);
        Resp data = (Resp) submit.getData();
        if (data!=null && data.getData() instanceof OrderEntityVo) {
            //produce a payment page
            PayVo payVo = new PayVo();
            String pay = alipayTemplate.pay(payVo);
            return Resp.ok(pay);
        }

        return submit;
    }

    @PostMapping("/paySuccessfully")
    public String paySuccess(String orderNum) {
        try {
            orderService.paySuccess(orderNum);
        } catch (Exception e) {
           return "failed";
        }
        return "ok";
    }


}
