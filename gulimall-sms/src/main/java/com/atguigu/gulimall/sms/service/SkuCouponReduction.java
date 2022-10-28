package com.atguigu.gulimall.sms.service;

import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.atguigu.gulimall.sms.to.SkuReductionTo;

import java.util.List;

public interface SkuCouponReduction {


    List<SkuCouponTo> getCoupons(Long sukId);

    List<SkuReductionTo> getReductions(Long sukId);
}
