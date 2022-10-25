package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品可以使用的优惠券
 */
@Data
public class SkuCouponVo {

    private Long skuId; //商品id

    private Long couponId; //优惠券id

    private String desc;//优惠券描述

    private BigDecimal amount;//优惠卷的金额



}
