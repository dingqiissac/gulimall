package com.atguigu.gulimall.sms.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.sms.to.SkuInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimall-pms")
public interface SpuFeignService {

    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoTo> info(@PathVariable("skuId") Long skuId);

}
