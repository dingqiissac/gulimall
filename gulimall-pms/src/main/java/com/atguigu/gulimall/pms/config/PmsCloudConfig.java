package com.atguigu.gulimall.pms.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class PmsCloudConfig {


    @Bean("mainThreadPool")
    public ThreadPoolExecutor threadPool() {

        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(
                        10, 1000, 3L, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE/3)
                );
        return threadPoolExecutor;
    }


}
