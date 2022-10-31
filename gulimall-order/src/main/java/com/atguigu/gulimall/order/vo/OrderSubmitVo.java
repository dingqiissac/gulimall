package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交上来的信息
 */
@Data
public class OrderSubmitVo {

    //提交上次订单确认页给你的令牌；
    private String orderToken;

    private String remark;//订单的备注

    private Long addressId;//选中的地址

    private BigDecimal totalPrice;//提交一个订单总额；

    private Integer payType;//0-在线支付  1-货到付款


    //我们购买的商品可以不用提交，我们会自己去购物车里面找到你需要买的所有商品；
}
