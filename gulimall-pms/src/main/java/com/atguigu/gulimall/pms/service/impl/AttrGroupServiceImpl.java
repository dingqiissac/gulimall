package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.entity.responseEntity.AttrGroupIdWithAttrsAndRelations;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrGroupDao;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.pms.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }
    @Transactional
    @Override
    public PageVo queryInfoByGroupId(QueryCondition queryCondition, Integer groupId) {
        IPage<AttrGroupEntity> page = new Query<AttrGroupEntity>().getPage(queryCondition);
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        attrGroupEntityQueryWrapper.eq("catelog_id",groupId);

        IPage<AttrGroupEntity> pageVo = this.page(page, attrGroupEntityQueryWrapper);
        //查出所有分组
        List<AttrGroupEntity> records = pageVo.getRecords();

        ArrayList<AttrGroupIdWithAttrsAndRelations> attrGroupIdWithAttrsAndRelations
                = new ArrayList<>(records.size());

        for (AttrGroupEntity record : records) {
            AttrGroupIdWithAttrsAndRelations attrGroupIdWithAttrsAndRelations1 = new AttrGroupIdWithAttrsAndRelations();
            //查出当前分组的所有的attrIds
            Long attrGroupId = record.getAttrGroupId();
            List<AttrAttrgroupRelationEntity> relationEntities =
                    attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
            ArrayList<Long> attrsId = new ArrayList<>();
            for (AttrAttrgroupRelationEntity relationEntity : relationEntities) {
                attrsId.add(relationEntity.getAttrId());
            }

            List<AttrEntity> attrs = new ArrayList<>();
            //根据 attrids 查出所有属性
            if(!CollectionUtils.isEmpty(attrsId)){
                attrs= attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrsId));
            }


            BeanUtils.copyProperties(record,attrGroupIdWithAttrsAndRelations1);
            attrGroupIdWithAttrsAndRelations1.setAttrEntities(attrs);

            attrGroupIdWithAttrsAndRelations.add(attrGroupIdWithAttrsAndRelations1);

        }

        return new PageVo(attrGroupIdWithAttrsAndRelations,(int)pageVo.getTotal(),(int)pageVo.getSize(),(int)pageVo.getCurrent());
    }

    @Transactional
    @Override
    public AttrGroupIdWithAttrsAndRelations queryAttrsByGroupId(Integer groupId) {
        AttrGroupIdWithAttrsAndRelations attrGroupIdWithAttrsAndRelations
                = new AttrGroupIdWithAttrsAndRelations();


        //先查询 attrGroupEntity
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(groupId);

        //再查询 相关 relations
        Long attrGroupId = attrGroupEntity.getAttrGroupId();
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelation = new QueryWrapper<>();
        attrAttrgroupRelation.eq("attr_group_id",attrGroupId);


        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities
                = attrAttrgroupRelationDao.selectList(attrAttrgroupRelation);


        //最后查询相关 attrs
        ArrayList<Long> attrs = new ArrayList<>();
        attrAttrgroupRelationEntities.forEach(item->{
            attrs.add(item.getAttrId());
        });
        List<AttrEntity> attrEntities = new ArrayList<AttrEntity>();
        if(!CollectionUtils.isEmpty(attrs)){
            attrEntities = attrDao.selectBatchIds(attrs);
        }

        //copy
        BeanUtils.copyProperties(attrGroupEntity,attrGroupIdWithAttrsAndRelations);
        attrGroupIdWithAttrsAndRelations.setAttrEntities(attrEntities);
        attrGroupIdWithAttrsAndRelations.setRelations(attrAttrgroupRelationEntities);


        return attrGroupIdWithAttrsAndRelations;
    }

}