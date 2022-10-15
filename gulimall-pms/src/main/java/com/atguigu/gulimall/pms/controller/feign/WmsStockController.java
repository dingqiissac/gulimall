package com.atguigu.gulimall.pms.controller.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.WareSkuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "gulimall-wms")
public interface WmsStockController {

    @PostMapping("wms/waresku/sku/query")
    public Resp<List<WareSkuVo>> queryStockBySkuId(@RequestBody List<Long> skuIds);
}
