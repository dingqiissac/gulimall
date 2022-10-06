package com.atguigu.gulimall.pms.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/world")
    public String info(){
        return "world";
    }
}
