package com.atguigu.gulimall.search.config;

import com.google.gson.GsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MyJestClient {


    @Bean
    public JestClient getClientConfig() {
        JestClientFactory factory = new JestClientFactory();
        //集群可以写多个节点，查询的时候不管配置一个还是多个节点，都会到整个集群中去查
        List<String> urlList = Arrays.asList("http://127.0.0.1:9200");
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(urlList) //参数可以是集群，请先定义一个list集合，将节点url分别添加到list
                //.defaultCredentials("elastic","changeme") //如果使用了x-pack，就要添加用户名和密码
                .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create())
                .multiThreaded(true) //多线程模式
                .connTimeout(60000) //连接超时
                .readTimeout(60000) //由于是基于http，所以超时时间必不可少，不然经常会遇到socket异常：read time out
                .build()); //更多参数请查看api
        JestClient client = factory.getObject();
        return client;
    }

}


