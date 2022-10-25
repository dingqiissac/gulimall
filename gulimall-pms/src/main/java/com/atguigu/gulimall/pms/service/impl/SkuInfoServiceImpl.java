package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.to.SkuInfoVo;
import com.atguigu.gulimall.pms.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.pms.entity.SkuSaleAttrValueEntity;
import org.springframework.beans.BeanUtils;
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

import com.atguigu.gulimall.pms.dao.SkuInfoDao;
import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import com.atguigu.gulimall.pms.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuListBySpuId(Long spuId) {
        QueryWrapper<SkuInfoEntity> skuInfoEntityQueryWrapper = new QueryWrapper<>();
        skuInfoEntityQueryWrapper.eq("spu_id", spuId);

        List<SkuInfoEntity> skuInfoEntities = this.baseMapper.selectList(skuInfoEntityQueryWrapper);

        return skuInfoEntities;
    }

    @Override
    public SkuInfoVo getSkuVo(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoDao.selectById(skuId);
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        skuInfoVo.setSkuId(skuInfoEntity.getSkuId());
        skuInfoVo.setPrice(skuInfoEntity.getPrice());
        skuInfoVo.setPics(skuInfoEntity.getSkuDefaultImg());

        List<SkuSaleAttrValueEntity> sku_id = skuSaleAttrValueDao.selectList(new QueryWrapper<SkuSaleAttrValueEntity>()
                .eq("sku_id", skuInfoEntity.getSkuId()));

        String meal = "";
        for (SkuSaleAttrValueEntity skuSaleAttrValueEntity : sku_id) {
            meal+="-"+skuSaleAttrValueEntity.getAttrValue();
        }
        skuInfoVo.setSetmeal(meal);//

        skuInfoVo.setSkuTitle(skuInfoEntity.getSkuTitle());

        BeanUtils.copyProperties(skuInfoEntity, skuInfoVo);
        return skuInfoVo;
    }

}