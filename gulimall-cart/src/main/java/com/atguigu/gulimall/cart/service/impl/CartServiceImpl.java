package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.cart.feign.SkuFeignService;
import com.atguigu.gulimall.cart.feign.SmsFeignService;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.*;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuInfoVo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Autowired
    SkuFeignService skuFeignService;

    @Autowired
    @Qualifier("mainThreadPool")
    ThreadPoolExecutor poolExecutor;

    @Autowired
    SmsFeignService smsFeignService;
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

        CartKey cartKey = getKey(userKey, authorization);
        //购物车在Redis中保存
        if (cartKey.isLogin()) {
            if (cartKey.isMerge()) {
                RMap<String, String> map = mergeCarts(userKey, Long.parseLong(cartKey.getKey()));
            }
        }else {
            cartVo.setUserKey(cartKey.getKey());
        }
        List<CartItemVo> itemsFromCart = getItemsFromCart(cartKey.getKey());

        cartVo.setItems(itemsFromCart);
        return cartVo;
    }

    private List<CartItemVo> getItemsFromCart(String key){
        ArrayList<CartItemVo> cartItemVos = new ArrayList<>();

        RMap<String, String> map = redisson.getMap(Constant.CART_PREFIX+key);
        //获取到所有的购物车里面的购物项
        Collection<String> values = map.values();
        if (values != null && values.size() > 0) {
            for (String value : values) {
                CartItemVo itemVo = JSON.parseObject(value, CartItemVo.class);
                cartItemVos.add(itemVo);
            }
        }
        return cartItemVos;
    }

    @Override
    public CartVo addToCart(Long skuId, Integer num, String userKey, String authorization) throws ExecutionException, InterruptedException {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();
        //1、获取购物车
        RMap<String, String> map = redisson.getMap(Constant.CART_PREFIX + cartKey);

        if (key.isLogin() && !StringUtils.isEmpty(userKey)) {
            // login status
            map = mergeCarts(userKey, Long.parseLong(cartKey));
        }

        //2、添加购物车之前先确定购物车中有没有这个商品，如果有就数量+1 如果没有新增
        CartItemVo resVo = addCartItemVo(skuId, num, cartKey);

        CartVo cartVo = new CartVo();

        cartVo.setItems(Arrays.asList(resVo));

        if (!key.isLogin()) {
            cartVo.setUserKey(cartKey);
            redisTemplate.expire(Constant.CART_PREFIX + cartKey, Constant.CART_TIMEOUT, TimeUnit.MINUTES);
        }
        return cartVo;
    }

    @Override
    public CartVo updateCart(Long skuId, Integer num, String userKey, String authorization) {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();

        RMap<String, String> map = redisson.getMap(Constant.CART_PREFIX + cartKey);
        String item = map.get(skuId.toString());
        CartItemVo vo = JSON.parseObject(item, CartItemVo.class);
        vo.setNum(num);

        map.put(skuId.toString(),JSON.toJSONString(vo));

        //get all items
        List<CartItemVo> itemsFromCart = getItemsFromCart(cartKey);
        CartVo cartVo = new CartVo();

        cartVo.setItems(itemsFromCart);

        return cartVo;
    }

    @Override
    public CartVo checkCart(String userKey, String authorization, Long[] skuIds, Integer status) {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();

        RMap<String, String> itemsMap = redisson.getMap(Constant.CART_PREFIX + cartKey);

        for (Long skuId : skuIds) {
            if(itemsMap.containsKey(skuId.toString())){
                String item = itemsMap.get(skuId.toString());
                CartItemVo vo = JSON.parseObject(item, CartItemVo.class);
                vo.setCheck(status==0?true:false);
                itemsMap.put(skuId.toString(),JSON.toJSONString(vo));
            }
        }

        List<CartItemVo> itemsFromCart = getItemsFromCart(cartKey);
        CartVo cartVo = new CartVo();

        cartVo.setItems(itemsFromCart);

        return cartVo;
    }

    private RMap<String, String> mergeCarts(String userKey, Long userId) {
        RMap<String, String> tempMap = redisson.getMap(Constant.CART_PREFIX + userKey);
        RMap<String, String> map = redisson.getMap(Constant.CART_PREFIX + userId);

        Set<String> itemsIds = tempMap.keySet();
        for (String itemsId : itemsIds) {
            if (map.containsKey(itemsId)) {
                //登入的map
                String onLineItem = (String) map.get(itemsId);
                CartItemVo onLineItemVo = JSON.parseObject(onLineItem, CartItemVo.class);
                //未登入的map
                String tempItem = (String) tempMap.get(itemsId);
                CartItemVo tempItemVo = JSON.parseObject(tempItem, CartItemVo.class);

                onLineItemVo.setNum(onLineItemVo.getNum() + tempItemVo.getNum());
                map.put(itemsId, JSON.toJSONString(onLineItemVo));
            } else {
                String tempItem = (String) tempMap.get(itemsId);
                CartItemVo tempItemVo = JSON.parseObject(tempItem, CartItemVo.class);

                map.put(itemsId, JSON.toJSONString(tempItemVo));
            }
        }

        tempMap.delete();

        return map;
    }

    private CartItemVo addCartItemVo(Long skuId, Integer num, String cartKey) throws ExecutionException, InterruptedException {
        RMap<String, Object> cart = redisson.getMap(Constant.CART_PREFIX + cartKey);

        CartItemVo vo = null;
        String item = (String) cart.get(skuId.toString());
        if (!StringUtils.isEmpty(item)) {
            CartItemVo itemVo = JSON.parseObject(item, CartItemVo.class);
            itemVo.setNum(itemVo.getNum() + num);
            cart.put(skuId.toString(), JSON.toJSONString(itemVo));
            vo = itemVo;
        } else {
            CartItemVo itemVo = new CartItemVo();
            //sku基本信息
            CompletableFuture<Void> firstTask = CompletableFuture.runAsync(() -> {
                //1、查询sku当前商品的详情；
                Resp<SkuInfoVo> sKuInfoForCart = skuFeignService.getSKuInfoForCart(skuId);
                SkuInfoVo data = sKuInfoForCart.getData();
                //2、购物项
                BeanUtils.copyProperties(data, itemVo);
                itemVo.setNum(num);
            },poolExecutor);

            //查询优惠卷
            CompletableFuture<Void> secondTask = CompletableFuture.runAsync(() -> {
                //remote获取当前优惠券信息
                Resp<List<SkuCouponTo>> coupons = smsFeignService.getCoupons(skuId);
                List<SkuCouponTo> couponData = coupons.getData();

                ArrayList<SkuCouponVo> skuCouponVos = new ArrayList<>();
                if (couponData != null && couponData.size() > 0) {
                    for (SkuCouponTo couponDatum : couponData) {
                        SkuCouponVo skuCouponVo = new SkuCouponVo();
                        BeanUtils.copyProperties(couponDatum, skuCouponVo);
                        skuCouponVos.add(skuCouponVo);
                    }
                }
                itemVo.setCoupons(skuCouponVos);
            }, poolExecutor);

            //查询满减
            CompletableFuture<Void> thirdTask = CompletableFuture.runAsync(() -> {
                Resp<List<SkuFullReductionVo>> reductions = smsFeignService.getReductions(skuId);
                List<SkuFullReductionVo> reductionData = reductions.getData();
                if(reductionData!=null && reductionData.size()>0){
                    itemVo.setReductions(reductionData);
                }
                }, poolExecutor);

            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(firstTask, secondTask, thirdTask);

            voidCompletableFuture.get();

            //3、保存购物车数据
            cart.put(skuId.toString(), JSON.toJSONString(itemVo));
            vo = itemVo;
        }
        return vo;
    }


    private CartKey getKey(String userKey, String authorization) {

        CartKey cartKey = new CartKey();
        String key = "";

        if (!StringUtils.isEmpty(authorization)) {
            //登录了;
            Map<String, Object> body = GuliJwtUtils.getJwtBody(authorization);
            Long id = Long.parseLong(String.valueOf(body.get("id")));
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