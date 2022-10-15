package com.atguigu.gulimall.commons.es;

import lombok.Data;

import java.io.Serializable;

@Data
public class EsProductAttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long productAttributeId;

    private String value;//3G

    private String name;//属性名

    private Long spuId;//属性对应的spuId



}
