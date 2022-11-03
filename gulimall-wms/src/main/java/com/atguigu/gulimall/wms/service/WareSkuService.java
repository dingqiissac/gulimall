package com.atguigu.gulimall.wms.service;

import com.atguigu.gulimall.wms.vo.LockStockVo;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 商品库存
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:28:19
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageVo queryPage(QueryCondition params);

    List<WareSkuEntity> queryStockBySkuId(Long skuId);

    List<WareSkuEntity> queryStockBySkuIds(List<Long> skuIds);

    LockStockVo lockAndCheckStock(List<SkuLockVo> skuIds);
}

