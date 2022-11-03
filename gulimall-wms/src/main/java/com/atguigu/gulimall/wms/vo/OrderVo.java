package com.atguigu.gulimall.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVo {

    private Long id;

    private Long memberId;

    private String orderSn;

    private Long couponId;

    private Date createTime;

    private String memberUsername;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal freightAmount;

    private BigDecimal promotionAmount;

    private BigDecimal integrationAmount;

    private BigDecimal couponAmount;

    private BigDecimal discountAmount;

    private Integer payType;

    private Integer sourceType;

    private Integer status;



}
