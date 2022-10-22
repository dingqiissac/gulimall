package com.atguigu.gulimall.commons.exception;

public class UserDuplicatedException extends RuntimeException{

    public UserDuplicatedException(){
        super("用户名异常");
    }

}
