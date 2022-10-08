package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.requestEntity.AttrEntityWithGroupId;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2019-08-01 15:52:32
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryPageUnderCatId(QueryCondition queryCondition, Integer catId);

    PageVo querySaleAttrPageUnderCatId(QueryCondition queryCondition, Integer catId);

    void saveAttrEntityAndRelationship(AttrEntityWithGroupId attr);
}

