package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.lang.reflect.Member;
import java.util.List;

@Data
public class OrderConfirmVo {

    //查出用户id的所有收货地址
    private List<MemberAddressVo> addresses;




    //private List<> products;
    //private List<CartItemVo> products;

    //获取用户在购物车中选中的需要购买的所有商品，以及价格优惠等信息
    private CartVo cartVo;


    //查出用户领取的所有些商品能用的优惠券

    //查出用户的购物可抵扣积分

    private Integer bounds = 99000;//用户可以抵扣的积分

    private String orderToken; //交易令牌，防止重复提交


}
