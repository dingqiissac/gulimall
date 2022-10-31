package com.atguigu.gulimall.order.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class PmsCloudConfig {


    @Bean("mainThreadPool")
    @Primary
    public ThreadPoolExecutor threadPool() {

        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(
                        10, 1000, 3L, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(1000000)
                );
        return threadPoolExecutor;
    }


}
