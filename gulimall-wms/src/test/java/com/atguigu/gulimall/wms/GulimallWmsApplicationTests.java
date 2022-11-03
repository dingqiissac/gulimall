package com.atguigu.gulimall.wms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class GulimallWmsApplicationTests {

    public static void main(String[] args) {
        AtomicInteger totalThreadCount = new AtomicInteger(3);
        IntStream.range(0, totalThreadCount.get()).forEach(item->{

            System.out.println(item);
        });
    }

    @Test
    public void contextLoads() {

    }

}
