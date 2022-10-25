package com.atguigu.gulimall.sms.to;

import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SkuCouponTo {
    private Long skuId; //商品id

    private Long couponId; //优惠券id

    private String desc;//优惠券描述

    private BigDecimal amount;//优惠卷的金额



}
