package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartVo;

public interface CartService {

    CartVo getCart(String userKey, String authorization);



    CartVo addToCart(Long skuId, Integer num, String userKey, String authorization);
}
