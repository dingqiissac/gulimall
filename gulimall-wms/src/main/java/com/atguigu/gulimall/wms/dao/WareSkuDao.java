package com.atguigu.gulimall.wms.dao;

import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:28:19
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<WareSkuEntity> checkStock(@Param("skuId") SkuLockVo skuId);

    Long lockStockBySkuIdAndWareId(@Param("wareSkuEntity")WareSkuEntity wareSkuEntity,@Param("num") Integer num);

    void updateRollBackStock(@Param("skuLock") SkuLock skuLock);

    void reduceStocks(@Param("skuLock") SkuLock skuLock);
}
