package com.atguigu.gulimall.pms.controller.feign;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.es.EsSkuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name ="gulimall-search")
public interface EsSpuToEsController {
    @PostMapping("/es/spu/up")
    public Resp<Object> spuUp(@RequestBody List<EsSkuVo> vo);

    @PostMapping("/es/spu/down")
    public Resp<Object> spuDown(@RequestBody List<EsSkuVo> vo);
}
