# 项目文档 

## Nacos使用

### 1、安装

自己参照文档下载解压运行

### 2、Nacos作为注册中心

- 1、导入nacos服务注册发现功能的jar包

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>${nacos.version}</version>
        </dependency>
```

- 2、开启服务注册发现功能

```java
@Configuration
@EnableDiscoveryClient
public class PmsCloudConfig {
}
```

- 3、编写application.properties配置，说明nacos的地址即可

```properties
spring.application.name=gulimall-pms
# 指定注册中心地址即可
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

- 4、测试远程调用
- 1、导入feign进行远程调用功能

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```

- 2、消费端的写法
  配置类

```java
@Configuration
@EnableDiscoveryClient  //开启服务注册发现功能
@EnableFeignClients(basePackages = "com.atguigu.gulimall.oms.feign") //开启feign的远程调用功能。
//配置feign接口所在的包
public class OmsCloudConfig {


}
```

```java
@Service
@FeignClient(name = "gulimall-pms")
public interface WorldService {

    @GetMapping("/world")  //
    public String world();
}


@RestController
public class HelloController {
    
    @Autowired
    WorldService worldService;

    /**
     * feign声明式调用
     * @return
     */
    @GetMapping("/hello")
    public String hello(){
        String msg = "";
        //远程调用gulimall-pms服务的 /world 请求对应的方法,并接受返回值
        msg = worldService.world();
        return "hello"+ msg;
    }
}

```

### 3、Nacos作为配置中心

配置中心：
    集中管理配置，配置动态更新，回滚配置....
    

- 1、 导包

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    <version>${latest.version}</version>
</dependency>
```

- 2、写配置
  创建一个 bootstrap.properties；
  SpringBoot默认规则，bootstrap.properties 里面的所有配置优先于 application.properties里面的所有配置进行解析

```properties
spring.application.name=gulimall-oms
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
```

- 3、默认规则；
  dataId:  配置文件名  application.properties
  ${prefix}-${spring.profile.active}.${file-extension}
  前缀-当前环境.文件扩展名；
  prefix 默认为 spring.application.name 的值； gulimall-oms
  file-extension：默认是properties

spring.profile.active 没有；注意：当 spring.profile.active 为空时，对应的连接符 - 也将不存在，dataId 的拼接格式变成 ${prefix}.${file-extension}
prefix.file-extension

gulimall-oms.properties

以上总结一句话，
只需要给nacos中创建一个DataId名叫 当前项目名.properties完事

- 4、 想要动态获取配置，一个注解
  @RefreshScope
- 5、nacos的命名空间等概念；
  命名空间：区分不同环境；
  默认情况，本项目在public下找 项目名.properties文件；

组：可以区分不同业务；不同业务属于不同组；

以上两个随便反着用也行；

最佳实战：
    使用namespace来区分不同的服务【每个服务有自己的名称空间】
    使用group来区分不同环境【dev,prod】