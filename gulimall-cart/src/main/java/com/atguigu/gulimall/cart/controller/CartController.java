package com.atguigu.gulimall.cart.controller;


import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.commons.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Api(tags = "购物车系统")
@RequestMapping("api/cart")
@RestController
public class CartController {


    @Autowired
    CartService cartService;


    @Autowired
    @Qualifier("nonMainThreadPool")
    ThreadPoolExecutor poolExecutor;


    @ApiOperation("返回购物车里所有选中的商品")
    @GetMapping("/getItemForOrder/{id}")
    public Resp<CartVo> selectCartWithStatus(@PathVariable(value = "id") Long id){

        CartVo res = cartService.selectCartWithStatus(id);

        return Resp.ok(res);
    }




    @ApiOperation("change chosen status")
    @GetMapping("/stop/non")
    public Resp<Object> stopPoolExecutor(){

        poolExecutor.shutdown();

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("queue",poolExecutor.getQueue().size());
        stringObjectHashMap.put("ActiveCount",poolExecutor.getActiveCount());

        return Resp.ok(stringObjectHashMap);
    }


    @ApiOperation("change chosen status")
    @PostMapping("/check")
    public Resp<CartVo> checkCart(String userKey,@RequestParam(value = "skuIds") Long[] skuIds,
                                  @RequestParam(value = "status")Integer status,
                                @RequestHeader(name = "Authorization",required = false) String authorization){


        CartVo cartVo =   cartService.checkCart(userKey,authorization,skuIds,status);

        return Resp.ok(cartVo);
    }


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

    @ApiOperation("更新购物车商品数量")
    @PostMapping("/update")
    public Resp<CartVo> updateCart(@RequestParam(name = "skuId",required = true) Long skuId,
                                   @RequestParam(name = "num",defaultValue = "1") Integer num,
                                   String userKey,
                                   @RequestHeader(name = "Authorization",required = false) String authorization){

        CartVo cartVo = cartService.updateCart(skuId,num,userKey,authorization);

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
                                  @RequestHeader(name = "Authorization",required = false) String authorization) throws ExecutionException, InterruptedException {

        //1、判断是否登录了
        CartVo cartVo = cartService.addToCart(skuId, num, userKey, authorization);


        Map<String,Object> map = new HashMap<>();
        map.put("userKey",cartVo.getUserKey());
        map.put("item",cartVo.getItems());
        return Resp.ok(map);
    }

}
