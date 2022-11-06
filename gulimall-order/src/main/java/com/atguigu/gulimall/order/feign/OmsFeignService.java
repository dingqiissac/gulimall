package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.order.OrderFeignSubmitVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gulimall-oms")
public interface OmsFeignService {


    @PostMapping("oms/order/createAndSave")
    public Resp<OrderEntityVo> createAndSaveOrder(@RequestBody OrderFeignSubmitVo orderSubmitVo);

    @PostMapping("oms/order/paySuccess")
    public Resp<String> paySuccess(@RequestParam("orderNum") String orderNum);


}

