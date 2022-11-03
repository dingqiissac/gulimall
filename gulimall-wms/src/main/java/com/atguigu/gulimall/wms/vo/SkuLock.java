package com.atguigu.gulimall.wms.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

@Data
public class SkuLock implements Serializable {


        private Long skuId;
        private Long wareId;
        private Integer locked;
        private Boolean success;
        private String orderToken;
}
