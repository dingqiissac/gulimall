package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.pms.entity.requestEntity.AttrEntityWithGroupId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrDao attrDao;

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageUnderCatId(QueryCondition queryCondition, Integer catId) {
        IPage<AttrEntity> page = new Query<AttrEntity>().getPage(queryCondition);

        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper.eq("catelog_id", catId);
        attrEntityQueryWrapper.eq("attr_type",1);

        IPage<AttrEntity> res = this.page(page, attrEntityQueryWrapper);

        return new PageVo(res);
    }

    @Override
    public PageVo querySaleAttrPageUnderCatId(QueryCondition queryCondition, Integer catId) {
        IPage<AttrEntity> page = new Query<AttrEntity>().getPage(queryCondition);

        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper.eq("catelog_id", catId);
        attrEntityQueryWrapper.eq("attr_type",0);

        IPage<AttrEntity> res = this.page(page, attrEntityQueryWrapper);
        return new PageVo(res);
    }


    @Transactional
    @Override
    public void saveAttrEntityAndRelationship(AttrEntityWithGroupId attr) {
        AttrEntity attrEntity = new AttrEntity();

        BeanUtils.copyProperties(attr,attrEntity);
        attrDao.insert(attrEntity);

        Long attrId = attrEntity.getAttrId();
        Long attrGroupId = attr.getAttrGroupId();

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
        attrAttrgroupRelationEntity.setAttrId(attrId);

        attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);

    }

}