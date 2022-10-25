package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartVo;

import java.util.concurrent.ExecutionException;

public interface CartService {

    CartVo getCart(String userKey, String authorization);



    CartVo addToCart(Long skuId, Integer num, String userKey, String authorization) throws ExecutionException, InterruptedException;

    CartVo updateCart(Long skuId, Integer num, String userKey, String authorization);

    CartVo checkCart(String userKey, String authorization, Long[] skuIds, Integer status);
}
