package com.atguigu.gulimall.search;

import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {

        Index build = new Index.Builder(new User("aaaaa","bbbbb",30)).index("user").type("info").id("1").build();


        DocumentResult execute = jestClient.execute(build);

        System.out.println("保存完成....");

    }

}
@NoArgsConstructor
@AllArgsConstructor
@Data
class User{
    private String username;
    private String email;
    private Integer age;
}
