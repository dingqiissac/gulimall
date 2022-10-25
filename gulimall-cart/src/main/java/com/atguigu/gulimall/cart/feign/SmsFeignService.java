package com.atguigu.gulimall.cart.feign;

import com.atguigu.gulimall.cart.vo.SkuCouponTo;
import com.atguigu.gulimall.cart.vo.SkuCouponVo;
import com.atguigu.gulimall.cart.vo.SkuFullReductionVo;
import com.atguigu.gulimall.cart.vo.SkuReductionTo;
import com.atguigu.gulimall.commons.bean.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-sms")
public interface SmsFeignService {

    @GetMapping("/sms/sku/coupon/{skuId}")
    public Resp<List<SkuCouponTo>> getCoupons(@PathVariable("skuId") Long sukId);


    @GetMapping("/sms/sku/reduction/{skuId}")
    public Resp<List<SkuFullReductionVo>> getReductions(@PathVariable("skuId") Long sukId);
}
