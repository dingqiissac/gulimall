package com.atguigu.gulimall.pms.controller.feign;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "gulimall-sms")
public interface SmsSaleInfoController {

    @PostMapping("sms/skubounds/saleInfo/save")
    public Resp<Object> saveSkuSaleInfos(@RequestBody List<SkuSaleInfoTo> sku);

}
