package com.atguigu.gulimall.pms.entity.requestEntity;

import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.pms.entity.detail.CouponsVo;
import com.atguigu.gulimall.pms.entity.detail.DetailAttrGroup;
import com.atguigu.gulimall.pms.entity.detail.DetailSaleAttrVo;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuItemDetailVo {

    //sku info

    private Long skuId;

    private Long spuId;

    private Long catalogId;

    private Long brandId;

    private String skuTitle;

    private String skuSubtitle;

    private BigDecimal price;

    private BigDecimal weight;

    //sku images
    private List<String> pics;

    //sku discount info
    private List<CouponsVo> coupons;
    //销售属性
    private List<DetailSaleAttrVo> saleAttrs;
    //spu基本属性
    private List<DetailAttrGroup> baseAttrs;
    //详情介绍
    private SpuInfoDescEntity desc;
}
