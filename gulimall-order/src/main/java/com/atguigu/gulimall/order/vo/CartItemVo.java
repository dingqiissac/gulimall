package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemVo {


    private Long skuId;//商品的id
    private String skuTitle;//商品的标题
    private String setmeal;//套餐

    private String pics;//商品图片

    private BigDecimal price;//单价
    private Integer num;//数量

    private BigDecimal totalPrice;//商品总价

    private boolean check = true;

    private List<SkuFullReductionVo> reductions;//商品满减信息，包含打折满减

    private List<SkuCouponVo> coupons;//优惠券



    private BigDecimal firstPrice;//老价格（第一次加入购物车的价格）

    private BigDecimal subPrice;//差价
}
