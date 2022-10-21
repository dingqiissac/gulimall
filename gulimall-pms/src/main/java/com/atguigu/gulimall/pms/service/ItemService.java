package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.requestEntity.SkuItemDetailVo;

import java.util.concurrent.ExecutionException;

public interface ItemService {

    SkuItemDetailVo getDetail(Long skuId) throws ExecutionException, InterruptedException;

}
