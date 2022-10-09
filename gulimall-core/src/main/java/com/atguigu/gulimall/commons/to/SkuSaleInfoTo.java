package com.atguigu.gulimall.commons.to;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SkuSaleInfoTo {

    private Long skuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
    private Integer[] work;

    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;

    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    public Integer calculate(){
       return work[3]*1+work[2]*2+work[1]*3+work[0]*8;
    }

}
