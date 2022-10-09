package com.atguigu.gulimall.oms.controller;

import com.atguigu.gulimall.oms.feign.PmsCalls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class TestController {

    @Autowired
    PmsCalls pmsCalls;

    @Value("${spring.datasource.username:default}")
    private String name;

    @GetMapping("/info")
    public String info1(){

        String info = pmsCalls.info();
        return "oms" + name;
    }

}
