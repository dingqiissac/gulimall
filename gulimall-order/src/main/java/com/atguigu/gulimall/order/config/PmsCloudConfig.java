package com.atguigu.gulimall.order.config;


import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class PmsCloudConfig {


    @Bean
    public JedisPool jedisPool(){
        JedisPool pool = new JedisPool("localhost",6379);
        return pool;
    }


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
