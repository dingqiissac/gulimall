package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.requestEntity.BaseAttrVo;
import com.atguigu.gulimall.pms.entity.requestEntity.SkuVo;
import com.atguigu.gulimall.pms.entity.requestEntity.SpuAllSave;
import com.atguigu.gulimall.pms.entity.requestEntity.UpdateBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * spu信息
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2019-08-01 15:52:32
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryPageByCatId(QueryCondition queryCondition, Integer catId);

    void updateStatusBySpuId(Integer spuId, Integer status);

    Boolean updateStatusByBatch(UpdateBatch updateBatch);

    void spuBigSaveAll(SpuAllSave spuInfo);

    Long saveSpuBaseInfo(SpuAllSave spuInfo);

    void saveSpuImages(Long spuId, String[] spuImages);

    void saveSpuBaseAttrs(Long spuId, List<BaseAttrVo> baseAttrs);

    void saveSkuInfos(Long spuId, List<SkuVo> skus);
}

