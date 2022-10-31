package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {

    private Integer totalCount; //总商品数量

    private BigDecimal totalPrice;//总商品价格

    private BigDecimal reductionPrice;//优惠了的价格

    private BigDecimal cartPrice;//购物车应该支付的价格

    private List<CartItemVo> items;//购物车中所有的购物项；
}
