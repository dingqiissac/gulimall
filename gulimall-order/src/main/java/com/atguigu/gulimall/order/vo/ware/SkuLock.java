package com.atguigu.gulimall.order.vo.ware;

import lombok.Data;

@Data
public class SkuLock {


        private Long skuId;
        private Long wareId;
        private Integer locked;
        private Boolean success;
}
