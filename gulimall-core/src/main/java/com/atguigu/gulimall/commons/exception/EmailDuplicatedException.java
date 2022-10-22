package com.atguigu.gulimall.commons.exception;

public class EmailDuplicatedException extends RuntimeException{

    public EmailDuplicatedException(){
        super("邮箱异常");
    }

}
