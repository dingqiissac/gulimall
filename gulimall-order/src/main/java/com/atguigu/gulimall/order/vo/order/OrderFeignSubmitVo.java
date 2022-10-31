package com.atguigu.gulimall.order.vo.order;

import com.atguigu.gulimall.order.vo.CartVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderFeignSubmitVo {

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
}
