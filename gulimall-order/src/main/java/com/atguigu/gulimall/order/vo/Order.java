package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Order {

    private Long orderId;
    private Integer status;
    private String desc;
}
