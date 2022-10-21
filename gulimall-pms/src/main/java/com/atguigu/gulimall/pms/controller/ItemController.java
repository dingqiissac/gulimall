package com.atguigu.gulimall.pms.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.entity.requestEntity.SkuItemDetailVo;
import com.atguigu.gulimall.pms.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
@RestController
@RequestMapping("api")
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping("/item/{skuId}.html")
    public Resp<SkuItemDetailVo> getSkuById(@PathVariable("skuId") Long skuId) throws ExecutionException, InterruptedException {

        SkuItemDetailVo skuItemDetailVo = itemService.getDetail(skuId);

        return Resp.ok(skuItemDetailVo);
    }

}
