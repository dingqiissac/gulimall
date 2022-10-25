package com.atguigu.gulimall.cart.controller;


import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.commons.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "购物车系统")
@RequestMapping("api/cart")
@RestController
public class CartController {


    @Autowired
    CartService cartService;


    /**
     * 以后购物车的所有操作，前端带两个令牌
     * 1）、登录以后的jwt放在请求头的 Authorization 字段（不一定带）
     * 2）、只要前端有收到服务器响应过一个userKey的东西，以后保存起来，访问所有请求都带上；（不一定）
     * @return
     */

    @ApiOperation("获取购物车中的数据")
    @GetMapping("/list")
    public Resp<CartVo> getCart(String userKey,
                                @RequestHeader(name = "Authorization",required = false) String authorization){


        CartVo cartVo =   cartService.getCart(userKey,authorization);

        return Resp.ok(cartVo);
    }


    /**
     *
     * @param skuId
     * @param num
     * @param userKey  临时用户的令牌。如果有就穿
     * @return
     */
    @ApiOperation("将某个sku添加到购物车")
    @PostMapping("/add")
    public Resp<Object> addToCart(@RequestParam(name = "skuId",required = true) Long skuId,
                                  @RequestParam(name = "num",defaultValue = "1") Integer num,
                                  String userKey,
                                  @RequestHeader(name = "Authorization",required = false) String authorization){

        //1、判断是否登录了
        CartVo cartVo = cartService.addToCart(skuId, num, userKey, authorization);


        Map<String,Object> map = new HashMap<>();
        map.put("userKey",cartVo.getUserKey());
        map.put("item",cartVo.getItems());
        return Resp.ok(map);
    }

}
