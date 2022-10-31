package com.atguigu.gulimall.order.vo.ware;

import lombok.Data;

@Data
public class SkuLockVo {
    private Long skuId;
    private Integer num;

    private String orderToken;
}
