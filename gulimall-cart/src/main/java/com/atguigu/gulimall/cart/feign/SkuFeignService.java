package com.atguigu.gulimall.cart.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("gulimall-pms")
public interface SkuFeignService {

    @GetMapping("/pms/skuinfo/cart/{skuId}")
    public Resp<SkuInfoVo> getSKuInfoForCart(@PathVariable("skuId") Long skuId);
}
