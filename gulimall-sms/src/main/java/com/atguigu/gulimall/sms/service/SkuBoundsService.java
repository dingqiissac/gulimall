package com.atguigu.gulimall.sms.service;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.sms.entity.SkuBoundsEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 商品sku积分设置
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:17:02
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saveSkuAllSaleInfo(List<SkuSaleInfoTo> sku);
}

