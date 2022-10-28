package com.atguigu.gulimall.sms.controller;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.service.SkuCouponReduction;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.atguigu.gulimall.sms.to.SkuReductionTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sms")
public class SkuCouponReductionController {

    @Autowired
    SkuCouponReduction skuCouponReduction;

    @GetMapping("/sku/coupon/{skuId}")
    public Resp<List<SkuCouponTo>> getCoupons(@PathVariable("skuId") Long sukId){

        List<SkuCouponTo> tos = skuCouponReduction.getCoupons(sukId);

        return Resp.ok(tos);
    }

    @GetMapping("/sku/reduction/{skuId}")
    public Resp<List<SkuReductionTo>> getReductions(@PathVariable("skuId") Long sukId){

        List<SkuReductionTo> tos = skuCouponReduction.getReductions(sukId);

        return Resp.ok(tos);
    }



}
