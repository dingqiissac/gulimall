package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.sms.dao.CouponDao;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SkuLadderDao;
import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import com.atguigu.gulimall.sms.feign.SpuFeignService;
import com.atguigu.gulimall.sms.service.SkuCouponReduction;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.atguigu.gulimall.sms.to.SkuInfoTo;
import com.atguigu.gulimall.sms.to.SkuReductionTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class SkuCouponReductionImpl implements SkuCouponReduction {

    @Autowired
    CouponDao couponDao;

    @Autowired
    SpuFeignService spuFeignService;

    @Autowired
    SkuLadderDao skuLadderDao;

    @Autowired
    SkuFullReductionDao skuFullReductionDao;


    @Override
    public List<SkuCouponTo> getCoupons(Long sukId) {
        //根据skuId 查出 couponId
        List<SkuCouponTo> couponTos = new ArrayList<>();

        Long spuId = null;
        Resp<SkuInfoTo> info = spuFeignService.info(sukId);
        SkuInfoTo data = info.getData();
        if (data != null) {
            spuId = data.getSpuId();
        }

        List<CouponEntity> tos = new ArrayList<>();
        if (spuId != null) {
            tos = couponDao.selectCouponsBySpuID(spuId);
        }

        if (tos != null && tos.size() > 0) {
            for (CouponEntity to : tos) {
                SkuCouponTo skuCouponTo = new SkuCouponTo();
                skuCouponTo.setAmount(to.getAmount());
                skuCouponTo.setCouponId(to.getId());
                skuCouponTo.setDesc(to.getCouponName());
                skuCouponTo.setSkuId(sukId);
                couponTos.add(skuCouponTo);
            }
        }

        return couponTos;
    }

    @Override
    public List<SkuReductionTo> getReductions(Long sukId) {
        List<SkuLadderEntity> ladderEntities = skuLadderDao
                .selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", sukId));


        List<SkuFullReductionEntity> fullReductionEntities = skuFullReductionDao
                .selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", sukId));

        List<SkuReductionTo> tos = new ArrayList<>();


        ladderEntities.forEach(item -> {
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item, skuReductionTo);
            skuReductionTo.setDesc("满" + item.getFullCount() + "享受" + item.getDiscount() + "优惠");
            skuReductionTo.setType(0);
            tos.add(skuReductionTo);
        });

        fullReductionEntities.forEach(item -> {
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item, skuReductionTo);
            skuReductionTo.setDesc("消费满" + item.getFullPrice() + "减免" + item.getReducePrice());
            skuReductionTo.setType(1);
            tos.add(skuReductionTo);
        });


        return tos;
    }
}
