package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SpuBoundsDao;
import com.atguigu.gulimall.sms.dao.SpuLadderDao;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SpuBoundsEntity;
import com.atguigu.gulimall.sms.entity.SpuLadderEntity;
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

import com.atguigu.gulimall.sms.dao.SkuBoundsDao;
import com.atguigu.gulimall.sms.entity.SkuBoundsEntity;
import com.atguigu.gulimall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {
    @Autowired
    SpuBoundsDao spuBoundsDao;

    @Autowired
    SpuLadderDao spuLadderDao;

    @Autowired
    SkuFullReductionDao skuFullReductionDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }
    @Transactional
    @Override
    public void saveSkuAllSaleInfo(List<SkuSaleInfoTo> skus) {
        if (skus != null && skus.size() > 0) {
            for (SkuSaleInfoTo skuSaleInfoTo : skus) {
                /**
                 *     private BigDecimal buyBounds;
                 *     private BigDecimal growBounds;
                 *     private Integer[] work;
                 */
                SpuBoundsEntity spuBoundsEntity = new SpuBoundsEntity();
                spuBoundsEntity.setBuyBounds(skuSaleInfoTo.getBuyBounds());
                spuBoundsEntity.setGrowBounds(skuSaleInfoTo.getGrowBounds());
                spuBoundsEntity.setSpuId(skuSaleInfoTo.getSkuId());
                spuBoundsEntity.setWork(skuSaleInfoTo.calculate());

                spuBoundsDao.insert(spuBoundsEntity);

                /**
                 *     private Integer fullCount;
                 *     private BigDecimal discount;
                 *     private Integer ladderAddOther;
                 */

                SpuLadderEntity spuLadderEntity = new SpuLadderEntity();
                spuLadderEntity.setSpuId(skuSaleInfoTo.getSkuId());
                spuLadderEntity.setFullCount(skuSaleInfoTo.getFullCount());
                spuLadderEntity.setDiscount(skuSaleInfoTo.getDiscount());
                spuLadderEntity.setAddOther(skuSaleInfoTo.getLadderAddOther());

                spuLadderDao.insert(spuLadderEntity);

                /**
                 *     private BigDecimal fullPrice;
                 *     private BigDecimal reducePrice;
                 *     private Integer fullAddOther;
                 */
                SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
                skuFullReductionEntity.setSkuId(skuSaleInfoTo.getSkuId());
                skuFullReductionEntity.setAddOther(skuSaleInfoTo.getFullAddOther());
                skuFullReductionEntity.setFullPrice(skuSaleInfoTo.getFullPrice());
                skuFullReductionEntity.setReducePrice(skuSaleInfoTo.getReducePrice());

                skuFullReductionDao.insert(skuFullReductionEntity);
            }
        }
    }

}