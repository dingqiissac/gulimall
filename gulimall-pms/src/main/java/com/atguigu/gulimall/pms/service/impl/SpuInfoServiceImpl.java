package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.controller.feign.SmsSaleInfoController;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.entity.requestEntity.*;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SmsSaleInfoController smsSaleInfoController;

    @Autowired
    SpuInfoDao spuInfoDao;

    @Autowired
    SpuInfoDescDao spuInfoDescDao;

    @Autowired
    ProductAttrValueDao productAttrValueDao;

    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    SkuImagesDao skuImagesDao;

    @Autowired
    AttrDao attrDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Integer catId) {
        //select * from table catlogid = 277 and (spu_name like ss or id = ss)
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<SpuInfoEntity>();
        if (catId != 0) {
            spuInfoEntityQueryWrapper.eq("catalog_id", catId);
        }
        if (!StringUtils.isEmpty(queryCondition.getKey())) {
            spuInfoEntityQueryWrapper.and(obj -> {
                obj.like("spu_name", queryCondition.getKey());
                obj.or().like("id", queryCondition.getKey());
                return obj;
            });
        }


        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);

        IPage<SpuInfoEntity> spuInfoEntityIPage = this.page(page, spuInfoEntityQueryWrapper);

        return new PageVo(spuInfoEntityIPage);
    }

    @Override
    public Boolean updateStatusBySpuId(Integer spuId, Integer status) {
        //update table set status = '0' where id = spuId
        UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<SpuInfoEntity>();

        updateWrapper.set("publish_status", status).eq("id", spuId);

        int update = spuInfoDao.update(null, updateWrapper);

        return update > 0 ? true : false;
    }

    @Override
    public Boolean updateStatusByBatch(UpdateBatch updateBatch) {
        UpdateWrapper<SpuInfoEntity> spuInfoEntityUpdateWrapper = new UpdateWrapper<>();
        spuInfoEntityUpdateWrapper.lambda()
                .in(SpuInfoEntity::getId, updateBatch.getSpuIds())
                .set(SpuInfoEntity::getPublishStatus, updateBatch.getStatus());
        int update = spuInfoDao.update(null, spuInfoEntityUpdateWrapper);

        return update > 0 ? true : false;
    }
    @GlobalTransactional
    @Override
    public void spuBigSaveAll(SpuAllSave spuInfo) {
        SpuInfoService spuInfoService = (SpuInfoService)AopContext.currentProxy();
        //1.1save spu basic data
        Long spuId = spuInfoService.saveSpuBaseInfo(spuInfo);
        //1.2 save spu images
        spuInfoService.saveSpuImages(spuId, spuInfo.getSpuImages());

        //save spu basic attrs data
        List<BaseAttrVo> baseAttrs = spuInfo.getBaseAttrs();
        spuInfoService.saveSpuBaseAttrs(spuId, baseAttrs);

        //save sku and relevant sale attr
        //feign inside
        spuInfoService.saveSkuInfos(spuId, spuInfo.getSkus());

        int m = 10/0;

        //save discount ticket

    }
    @Transactional
    @Override
    public Long saveSpuBaseInfo(SpuAllSave spuInfo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        spuInfoEntity.setUodateTime(new Date());
        spuInfoEntity.setCreateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }
    @Transactional
    @Override
    public void saveSpuImages(Long spuId, String[] spuImages) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String spuImage : spuImages) {
            stringBuffer.append(spuImage);
            stringBuffer.append(",");
        }

        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(stringBuffer.toString());

        spuInfoDescDao.insert(spuInfoDescEntity);
    }
    @Transactional
    @Override
    public void saveSpuBaseAttrs(Long spuId, List<BaseAttrVo> baseAttrs) {


        ArrayList<ProductAttrValueEntity> productAttrValueEntities =
                new ArrayList<ProductAttrValueEntity>(baseAttrs.size());

        baseAttrs.forEach(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuId);
            productAttrValueEntity.setAttrId(item.getAttrId());
            productAttrValueEntity.setAttrName(item.getAttrName());
            String[] valueSelected = item.getValueSelected();
            String values = AppUtils.splitStringWithSeparator(valueSelected, ",");
            productAttrValueEntity.setAttrValue(values);
            productAttrValueEntity.setAttrSort(0);
            productAttrValueEntity.setQuickShow(1);
            productAttrValueEntities.add(productAttrValueEntity);
//            productAttrValueDao.insert(productAttrValueEntity);
        });

        productAttrValueDao.insertBatch(productAttrValueEntities);
    }
    @Transactional
    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        //query spuInfo by spuId
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);
        ArrayList<SkuSaleInfoTo> skuSaleInfoTos = new ArrayList<>();
        for (SkuVo skuVo : skus) {
            //保存sku 基本信息

            String[] images = skuVo.getImages();

            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());

            skuInfoEntity.setPrice(skuVo.getPrice());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            if (images != null && images.length > 0) {
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuTitle(skuVo.getSkuTitle());
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setWeight(skuVo.getWeight());

            skuInfoDao.insert(skuInfoEntity);

            //保存images

            Long skuId = skuInfoEntity.getSkuId();

            for (int i = 0; i < images.length; i++) {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setImgUrl(images[i]);
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgSort(0);
                skuImagesEntity.setDefaultImg(i == 0 ? 1 : 0);

                skuImagesDao.insert(skuImagesEntity);
            }

            //当前sku所有销售属性
            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrs();
            //attrDao
            for (SaleAttrVo saleAttr : saleAttrs) {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                skuSaleAttrValueEntity.setAttrId(saleAttr.getAttrId());
                skuSaleAttrValueEntity.setAttrValue(saleAttr.getAttrValue());
                skuSaleAttrValueEntity.setSkuId(skuId);
                skuSaleAttrValueEntity.setAttrSort(0);

                //query attrName for setting
                AttrEntity attrEntity = attrDao.selectById(saleAttr.getAttrId());

                skuSaleAttrValueEntity.setAttrName(attrEntity.getAttrName());

                skuSaleAttrValueDao.insert(skuSaleAttrValueEntity);

            }
            //一下都是由sms完成 创建TO
            SkuSaleInfoTo skuSaleInfoTo = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo, skuSaleInfoTo);
            skuSaleInfoTo.setSkuId(skuId);
            skuSaleInfoTos.add(skuSaleInfoTo);
            //send data to SMS system

        }
        log.info("pms ready to send data 发出数据 ...{}",skuSaleInfoTos);
        smsSaleInfoController.saveSkuSaleInfos(skuSaleInfoTos);
        log.info("pms 发出数据 done...");


    }


}