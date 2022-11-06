package com.atguigu.gulimall.order.vo.pay;

import lombok.Data;

@Data
public class PayVo {
    private String out_trade_no;
    private String subject;
    private String total_amount;
    private String body;

}
