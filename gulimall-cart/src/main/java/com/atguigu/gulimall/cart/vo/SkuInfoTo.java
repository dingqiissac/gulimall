package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuInfoTo {


    private Long skuId;

    private Long spuId;

    private String skuCode;
    private String skuName;

    private String skuDesc;

    private Long catalogId;

    private Long brandId;

    private String skuDefaultImg;

    private String skuTitle;

    private String skuSubtitle;

    private BigDecimal price;

    private BigDecimal weight;
}
