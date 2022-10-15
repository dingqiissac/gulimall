package com.atguigu.gulimall.wms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.wms.dao.WareSkuDao;
import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<WareSkuEntity> queryStockBySkuId(Long skuId) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        wareSkuEntityQueryWrapper.eq("sku_id",skuId);

        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(wareSkuEntityQueryWrapper);

        return wareSkuEntities;
    }

    @Override
    public List<WareSkuEntity> queryStockBySkuIds(List<Long> skuIds) {
        List<WareSkuEntity> skuStocks = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds));
        return skuStocks;
    }

}