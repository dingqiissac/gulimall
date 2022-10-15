package com.atguigu.gulimall.commons.es;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EsSkuVo implements Serializable {
    private static final long serialVersionUID = -1L;
    private Long id;  //skuId
    private Long brandId;
    private String brandName;

    private Long productCategoryId;
    private String productCategoryName;
    private String pic;
    private String name;//这是需要检索的字段 分词
    private BigDecimal price;//sku-price；
    private Integer sale;//sku-sale
    private Integer stock;//sku-stock
    private Integer sort;//排序

    private List<EsProductAttributeValue> attrValueList;//商品的筛选属性(SPU的属性;
}
