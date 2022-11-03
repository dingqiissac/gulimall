package com.atguigu.gulimall.oms.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交上来的信息
 */
@Data
public class OrderSubmitVo {

    private String orderToken;

    private String remark;//订单的备注

    private Long addressId;//选中的地址

    private BigDecimal totalPrice;//提交一个订单总额；

    private Integer payType;//0-在线支付  1-货到付款

    private CartVo cartVo;

    private Long userId;


    private String billReceiverEmail;
    private String receiverName;
    private String receiverPhone;

    private String receiverPostCode;

    private String receiverProvince;

    private String receiverCity;

    private String receiverRegion;

    private String receiverDetailAddress;
    //我们购买的商品可以不用提交，我们会自己去购物车里面找到你需要买的所有商品；
}
