package com.atguigu.gulimall.cart.vo;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 */

public class CartVo {

    private Integer total; //总商品数量

    private BigDecimal totalPrice;//总商品价格

    private BigDecimal reductionPrice;//优惠了的价格

    private BigDecimal cartPrice;//购物车应该支付的价格

    @Getter
    @Setter
    private List<CartItemVo> items;//购物车中所有的购物项；

    @Getter
    @Setter
    private String userKey;//临时用户的key

    public Integer getTotal() {
        Integer num = 0;
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                num += item.getNum();
            }
        }
        return num;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal decimal = new BigDecimal("0.0");

        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                BigDecimal totalPrice = item.getTotalPrice();

                decimal = decimal.add(totalPrice);
            }
        }


        return decimal;
    }


    //减多少
    public BigDecimal getReductionPrice() {
        BigDecimal reduce = new BigDecimal("0.0");
        //拿到每一项的满减信息和优惠信息
        for (CartItemVo item : items) {

            List<SkuFullReductionVo> reductions = item.getReductions();
            //计算满减打折等可以减掉的金额
            if (reductions != null && reductions.size() > 0) {
                for (SkuFullReductionVo reduction : reductions) {
                    Integer type = reduction.getType();
                    if (type == 0) {
                        //0-打折  1-满减
                        Integer fullCount = reduction.getFullCount();
                        Integer discount = reduction.getDiscount();
                        if (item.getNum() >= fullCount) {
                            //折后价
                            BigDecimal reduceTp = item.getTotalPrice().multiply(new BigDecimal("0." + discount));
                            //减了这么多
                            BigDecimal subtract = item.getTotalPrice().subtract(reduceTp);
                            //累加了折后价
                            reduce = reduce.add(subtract);
                        }
                    }
                    if (type == 1) {
                        BigDecimal fullPrice = reduction.getFullPrice();
                        BigDecimal reducePrice = reduction.getReducePrice();

                        //-1 0 1
                        if ((item.getTotalPrice().subtract(fullPrice).compareTo(new BigDecimal("0.0")) > -1)) {
                            //累加了优惠价
                            reduce = reduce.add(reducePrice);
                        }
                    }
                }
            }


            //计算优惠券可以减掉的金额
            List<SkuCouponVo> coupons = item.getCoupons();
            if (coupons != null && coupons.size() > 0) {
                for (SkuCouponVo coupon : coupons) {
                    BigDecimal amount = coupon.getAmount();
                    reduce = reduce.add(amount);

                }
            }
        }

        return reduce;
    }

    //减后的价格
    public BigDecimal getCartPrice() {
        BigDecimal reductionPrice = getReductionPrice();
        BigDecimal totalPrice = getTotalPrice();
        BigDecimal subtract = totalPrice.subtract(reductionPrice);
        return subtract;
    }
}
