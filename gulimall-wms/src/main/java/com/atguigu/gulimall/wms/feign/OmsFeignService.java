package com.atguigu.gulimall.wms.feign;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.wms.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-oms")
public interface OmsFeignService {

    @PostMapping("oms/order/findByOrderNum")
    public Resp<OrderVo> findByOrderNum(@RequestParam("orderToken") String orderToken);

    @PostMapping("oms/order/deleteOrder")
    public Resp<String> deleteOrderAndItems(@RequestParam("num") String num);
}
