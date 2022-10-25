package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.cart.feign.SkuFeignService;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItemVo;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuInfoVo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Autowired
    SkuFeignService skuFeignService;

    /**
     * 获取购物车
     *
     * @param userKey
     * @param authorization
     * @return
     */
    @Override
    public CartVo getCart(String userKey, String authorization) {

        CartVo cartVo = new CartVo();

        List<CartItemVo> cartItemVos = new ArrayList<>();
        CartKey cartKey = getKey(userKey, authorization);
        //购物车在Redis中保存
        String key = Constant.CART_PREFIX + cartKey.getKey();

        //1、获取购物车
        RMap<String, Object> map = redisson.getMap(key);
        //判断如果两个都有需要合并购物车
        if (cartKey.isMerge() == true) {
            //如果需要合并。合并

        } else {
            //获取到所有的购物车里面的购物项
            Collection<Object> values = map.values();
            if (values != null && values.size() > 0) {
                for (Object value : values) {
                    String json = (String) value;
                    CartItemVo itemVo = JSON.parseObject(json, CartItemVo.class);
                    cartItemVos.add(itemVo);
                }
            }

        }

        cartVo.setItems(cartItemVos);
        return cartVo;
    }

    @Override
    public CartVo addToCart(Long skuId, Integer num, String userKey, String authorization) {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();
        //1、获取购物车
        RMap<String, Object> map = redisson.getMap(Constant.CART_PREFIX + cartKey);


        CartItemVo vo = null;
        //2、添加购物车之前先确定购物车中有没有这个商品，如果有就数量+1 如果没有新增
        String item = (String) map.get(skuId.toString());
        if (!StringUtils.isEmpty(item)) {
            CartItemVo itemVo = JSON.parseObject(item, CartItemVo.class);
            itemVo.setNum(itemVo.getNum() + num);
            map.put(skuId.toString(), JSON.toJSONString(itemVo));
            vo = itemVo;
        } else {
            //1、查询sku当前商品的详情；
            Resp<SkuInfoVo> sKuInfoForCart = skuFeignService.getSKuInfoForCart(skuId);
            SkuInfoVo data = sKuInfoForCart.getData();
            //2、购物项
            CartItemVo itemVo = new CartItemVo();
            BeanUtils.copyProperties(data, itemVo);
            itemVo.setNum(num);

            //3、保存购物车数据
            map.put(skuId.toString(), JSON.toJSONString(itemVo));
            vo = itemVo;
        }



        CartVo cartVo = new CartVo();
        cartVo.setUserKey(cartKey);

        cartVo.setItems(Arrays.asList(vo));

        if (!key.isLogin()) {
            redisTemplate.expire(Constant.CART_PREFIX + cartKey, Constant.CART_TIMEOUT, TimeUnit.MINUTES);
        }
        return cartVo;
    }


    private CartKey getKey(String userKey, String authorization) {

        CartKey cartKey = new CartKey();
        String key = "";

        if (!StringUtils.isEmpty(authorization)) {
            //登录了;
            Map<String, Object> body = GuliJwtUtils.getJwtBody(authorization);
            Long id = (Long) body.get("id");
            key = id + "";
            cartKey.setKey(key);
            cartKey.setLogin(true);
            if (!StringUtils.isEmpty(userKey)) {
                cartKey.setMerge(true);
            }
        } else {
            //没登录
            //第一次啥都没有
            if (!StringUtils.isEmpty(userKey)) {
                key = userKey;
                cartKey.setLogin(false);
                cartKey.setMerge(false);
            } else {
                key = UUID.randomUUID().toString().replace("-", "");
                cartKey.setLogin(false);
                cartKey.setMerge(false);
                cartKey.setTemp(true);//这是一个临时

            }

        }

        cartKey.setKey(key);
        return cartKey;

    }
}

@Data
class CartKey {
    private String key;
    private boolean login;
    private boolean temp;
    private boolean merge;

}