package com.atguigu.gulimall.cart.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项数据
 */

public class CartItemVo {

    @Setter  @Getter
    private Long skuId;//商品的id
    @Setter  @Getter
    private String skuTitle;//商品的标题
    @Setter  @Getter
    private String setmeal;//套餐

    @Setter  @Getter
    private String pics;//商品图片

    @Setter  @Getter
    private BigDecimal price;//单价
    @Setter  @Getter
    private Integer num;//数量

    private BigDecimal totalPrice;//商品总价

    @Setter  @Getter
    private List<SkuFullReductionVo> reductions;//商品满减信息，包含打折满减

    @Setter  @Getter
    private List<SkuCouponVo> coupons;//优惠券

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(num));
    }
}

