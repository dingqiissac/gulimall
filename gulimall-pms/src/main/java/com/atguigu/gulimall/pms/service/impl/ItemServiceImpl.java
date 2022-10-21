package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.entity.SkuImagesEntity;
import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.pms.entity.requestEntity.SkuItemDetailVo;
import com.atguigu.gulimall.pms.service.ItemService;
import com.atguigu.gulimall.pms.service.SkuImagesService;
import com.atguigu.gulimall.pms.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    @Qualifier("mainThreadPool")
    ThreadPoolExecutor poolExecutor;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescServiceImpl spuInfoDescService;



    @Override
    public SkuItemDetailVo getDetail(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemDetailVo skuItemDetailVo = new SkuItemDetailVo();
        //sku info
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity byId = skuInfoService.getById(skuId);
            return byId;
        }, poolExecutor);
        CompletableFuture<Void> voidCompletableFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            BeanUtils.copyProperties(skuInfoEntity, skuItemDetailVo);
        },poolExecutor);

        //sku images
        CompletableFuture<List<SkuImagesEntity>> SkuImagesEntitysFuture = CompletableFuture.supplyAsync(() -> {
            List<SkuImagesEntity> skus = skuImagesService
                    .list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            return skus;
        }, poolExecutor);
        CompletableFuture<Void> voidCompletableFuture1 = SkuImagesEntitysFuture.thenAcceptAsync((t) -> {
            ArrayList<String> pics = new ArrayList<>();
            t.forEach((item) -> {
                pics.add(item.getImgUrl());
            });
            skuItemDetailVo.setPics(pics);
        },poolExecutor);

        //sku discount info
        //销售属性
        //spu基本属性
        //详情介绍
        CompletableFuture<Void> voidCompletableFuture2 = skuInfoFuture.thenAcceptAsync((sku) -> {
            Long spuId = sku.getSpuId();
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
            skuItemDetailVo.setDesc(spuInfoDescEntity);
        }, poolExecutor);
        //异步
        //缓存


        CompletableFuture<Void> voidCompletableFuture3 = CompletableFuture.allOf(skuInfoFuture,
                SkuImagesEntitysFuture, voidCompletableFuture,
                voidCompletableFuture1, voidCompletableFuture2);


        voidCompletableFuture3.get();
        return skuItemDetailVo;
    }
}
