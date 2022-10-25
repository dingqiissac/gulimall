package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuFullReductionVo {

    //   id  sku_id  full_price  reduce_price  add_other
    private Long id;
    private Long skuId;
    private String desc;//描述
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer addOther;

    private Integer fullCount;//满几件
    private Integer discount;//打几折

    private Integer type;//满减的类型，满几件打几折，还是满xxx减xxx元；  0-打折  1-满减

}
