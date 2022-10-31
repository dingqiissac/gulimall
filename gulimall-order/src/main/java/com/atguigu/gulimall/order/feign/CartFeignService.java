package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.CartVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("api/cart/getItemForOrder/{id}")
    public Resp<CartVo> selectCartWithStatus(@PathVariable(value = "id") Long id);

}
