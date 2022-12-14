package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.responseEntity.AttrGroupIdWithAttrsAndRelations;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2019-08-01 15:52:32
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryInfoByGroupId(QueryCondition queryCondition, Integer groupId);

    AttrGroupIdWithAttrsAndRelations queryAttrsByGroupId(Integer groupId);
}

