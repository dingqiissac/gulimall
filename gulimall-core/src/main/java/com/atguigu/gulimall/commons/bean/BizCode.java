package com.atguigu.gulimall.commons.bean;


import lombok.Data;


public enum  BizCode {



    TOKEN_INVAILIED(40003,"令牌失效"),


    ORDER_NEED_REFRESH(41000,"订单数据有修改，请重新提交再试"),

    STOCK_NOT_ENOUGH(50001,"库存不足"),

    SERVICE_UNAVAILABLE(10000,"远程服务故障");

    Integer code;String msg;
    BizCode(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}
