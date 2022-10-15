package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.es.EsProductAttributeValue;
import com.atguigu.gulimall.commons.es.EsSkuVo;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.commons.to.WareSkuVo;
import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.controller.feign.EsSpuToEsController;
import com.atguigu.gulimall.pms.controller.feign.SmsSaleInfoController;
import com.atguigu.gulimall.pms.controller.feign.WmsStockController;
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
import org.springframework.util.CollectionUtils;

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

    @Autowired
    EsSpuToEsController esSpuToEsController;

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    WmsStockController wmsStockController;

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
    public void updateStatusBySpuId(Integer spuId, Integer status) {
        //上架 放在es 检索里：下架 就是删除。。。成功之后 修改db
        SpuInfoEntity spuInfoEntity = spuInfoDao.selectById(spuId);
        BrandEntity brandEntity = brandDao.selectById(spuInfoEntity.getBrandId());
        CategoryEntity categoryEntity = categoryDao.selectById(spuInfoEntity.getCatalogId());

        ArrayList<EsSkuVo> esSkuVos = new ArrayList<>();
        List<SkuInfoEntity> skus = skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        ArrayList<Long> longs = new ArrayList<>();
        skus.forEach(skuId -> {
            longs.add(skuId.getSkuId());
        });

        //查询库存wms
        Resp<List<WareSkuVo>> listResp = wmsStockController.queryStockBySkuId(longs);
        List<WareSkuVo> skuData = listResp.getData();

        //查出spu 属性
        List<ProductAttrValueEntity> spuAttrs = productAttrValueDao.
                selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<Long> attrs = new ArrayList<>();
        spuAttrs.forEach(item -> {
            attrs.add(item.getAttrId());
        });

        List<AttrEntity> attrEntities = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrs).eq("search_type", 1));
        ArrayList<EsProductAttributeValue> esProductAttributeValues = new ArrayList<>();
        attrEntities.forEach(item -> {
            spuAttrs.forEach(s -> {
                if (item.getAttrId() == s.getAttrId()) {
                    EsProductAttributeValue esProductAttributeValues1 = new EsProductAttributeValue();
                    esProductAttributeValues1.setId(s.getId());
                    esProductAttributeValues1.setName(s.getAttrName());
                    esProductAttributeValues1.setProductAttributeId(s.getAttrId());
                    esProductAttributeValues1.setSpuId(Long.valueOf(String.valueOf(spuId)));
                    esProductAttributeValues1.setValue(s.getAttrValue());
                    esProductAttributeValues.add(esProductAttributeValues1);
                }
            });
        });
        //productAttrValueEntities to


        if (!CollectionUtils.isEmpty(skus)) {
            //装配 封装 list
            skus.forEach(sku -> {
                EsSkuVo esSkuVo = new EsSkuVo();
                EsSkuVo encapsulate = encapsulate(esSkuVo, sku, brandEntity, categoryEntity, skuData, esProductAttributeValues);
                esSkuVos.add(encapsulate);
            });

            //开始上架或下架
            if (status == 0) {
                //下架
                soldOut(spuId, status, esSkuVos);
            } else {
                //上架
                soldIn(spuId, status, esSkuVos);
            }
        }


    }

    private EsSkuVo encapsulate(EsSkuVo esSkuVo, SkuInfoEntity sku, BrandEntity brandEntity,
                                CategoryEntity categoryEntity,
                                List<WareSkuVo> skuData, ArrayList<EsProductAttributeValue> productAttrValueEntities) {
        esSkuVo.setId(sku.getSkuId());
        esSkuVo.setBrandId(sku.getBrandId());
        //查找品牌name

        if (brandEntity != null) {
            esSkuVo.setBrandName(brandEntity.getName());
        }
        esSkuVo.setName(sku.getSkuTitle());
        esSkuVo.setPrice(sku.getPrice());
        esSkuVo.setPic(sku.getSkuDefaultImg());
        esSkuVo.setProductCategoryId(sku.getCatalogId());
        //查找分类名

        if (categoryEntity != null) {
            esSkuVo.setProductCategoryName(categoryEntity.getName());
        }
        esSkuVo.setSale(0);
        esSkuVo.setSort(0);

        skuData.forEach(item -> {
            if (item.getSkuId() == esSkuVo.getId()) {
                esSkuVo.setStock(item.getStock());
            }
        });

        esSkuVo.setAttrValueList(productAttrValueEntities);

        return esSkuVo;
    }

    private void updateStatusSpuId(Integer spuId, Integer status) {
        UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<SpuInfoEntity>();
        updateWrapper.set("publish_status", status).eq("id", spuId);
        spuInfoDao.update(null, updateWrapper);
    }

    //下架
    private void soldOut(Integer spuId, Integer status, List esSkuVos) {
        Resp resp = esSpuToEsController.spuDown(esSkuVos);
        if (resp.getCode() == 0) {
            updateStatusSpuId(spuId, status);
        }
    }

    //上架
    private void soldIn(Integer spuId, Integer status, List esSkuVos) {
        Resp resp = esSpuToEsController.spuUp(esSkuVos);
        if (resp.getCode() == 0) {
            updateStatusSpuId(spuId, status);
        }

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
        SpuInfoService spuInfoService = (SpuInfoService) AopContext.currentProxy();
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
        log.info("pms ready to send data 发出数据 ...{}", skuSaleInfoTos);
        smsSaleInfoController.saveSkuSaleInfos(skuSaleInfoTos);
        log.info("pms 发出数据 done...");


    }


}