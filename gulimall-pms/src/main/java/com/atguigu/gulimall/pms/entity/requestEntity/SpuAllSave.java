package com.atguigu.gulimall.pms.entity.requestEntity;

import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import lombok.Data;
import java.util.List;

@Data
public class SpuAllSave extends SpuInfoEntity {
    //spu IMAGES
    private String[] spuImages;

    //当前所有spu基本属性
    private List<BaseAttrVo> baseAttrs;

    //当前spu对应所有sku信息
    private List<SkuVo> skus;
}
