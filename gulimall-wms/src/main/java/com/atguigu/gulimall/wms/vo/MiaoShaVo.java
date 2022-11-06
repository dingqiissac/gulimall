package com.atguigu.gulimall.wms.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MiaoShaVo implements Serializable {

    String token;

    Long skuId;

    Long userId;

}
