package com.atguigu.gulimall.commons.exception;

public class PassWordAndUserNameInvlidationException extends RuntimeException{

    public PassWordAndUserNameInvlidationException(){
        super("用户名密码异常");
    }

}
