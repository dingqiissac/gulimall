package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.entity.requestEntity.RelationsAttrsAndGroupId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.pms.service.AttrAttrgroupRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageVo(page);
    }

    @Transactional
    @Override
    public Boolean deleteRelationsAttrsAndGroupId(RelationsAttrsAndGroupId attrsAndGroupId) {
        Long attrGroupId = attrsAndGroupId.getAttrGroupId();
        Long attrId = attrsAndGroupId.getAttrId();

        //删除 relation 先
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelation =
                new QueryWrapper<AttrAttrgroupRelationEntity>();
        attrAttrgroupRelation.eq("attr_group_id",attrGroupId).eq("attr_id",attrId);
        int relationDelete = attrAttrgroupRelationDao.delete(attrAttrgroupRelation);

        if(relationDelete>0){
            return true;
        }

        return false;
    }

}