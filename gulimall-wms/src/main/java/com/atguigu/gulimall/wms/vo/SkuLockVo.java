package com.atguigu.gulimall.wms.vo;

import lombok.Data;

@Data
public class SkuLockVo {
    private Long skuId;
    private Integer num;
    private String orderToken;
}
