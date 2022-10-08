package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.requestEntity.UpdateBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


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

    Boolean updateStatusBySpuId(Integer spuId, Integer status);

    Boolean updateStatusByBatch(UpdateBatch updateBatch);
}

