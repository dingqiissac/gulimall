package com.atguigu.gulimall.wms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallWmsApplicationTests {

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void contextLoads() {
        RSemaphore semaphore = redissonClient.getSemaphore("123");
        boolean b = semaphore.tryAcquire();

        System.out.println(b);

        semaphore.release();


    }

}
