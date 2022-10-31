package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuCouponVo {

    private Long skuId; //商品id

    private Long couponId; //优惠券id

    private String desc;//优惠券描述

    private BigDecimal amount;//优惠卷的金额

}
