package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.pay.PayVo;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

//@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016092200568607";
    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCduk0+Bh5uwy7N9AS7a+ZGUMfZtc1026PHQINJLbNe3rnafKAvqojoJdXHuhuUosYhA4crJEDHVAtupS/hCgTrKj+OFsrVCoFunsabji3sXFTT9Od4m8F6k0XWHXYJQHBd8y7Uq5ukDaVo7TbITZ0rJrzQZSlOKech1gih8afXusgPEMW6nq/atb3Nhm2mT+2g3aHQ9sA1NOCO1cXhjfQTdG0rnbFdSS97iWVnJFy4X4X81RcKzLHED42nCOsIFRJcjMWvjrNgNgufdIL3TO6fwxs7SiZgA6lNKhgr7osLN3fWn23+G1rehB7amEhIXh1bF8FidqH4LnM7VWMRkMSzAgMBAAECggEACN7/lSt27rffnAnLclchPncwplgJVDc+QOip+aWuXvmb7mPwBn5K8POf0HnxLyzwg87U/WIumodplTGJPdj4admYdX9mrRDIMtaPSRR4FMEkrD8r/aaU/TXnSaGij+al60LQMXntaQ6zbPFakW18F384Q2hNKG5G56pIFgOncwShSi7J5uQ/okWjHPFmtqR0dNBxbgRYOr5C4hoKr22TwhUuTUP9fShceDVisihDQdcERAMJVos0wkqGQ8NXpppmZB4mEHIRih2K3iWlUXLeQDCFChvhHkug9GK6uuIqoPs7GQ8k8nyr/vQ3FuqULARLAPTiG4odJyHal1BNHfIXQQKBgQD1PAUGEz8Nkn2JyjJuBjWdSOUJkU1jCDngWg6chPM93Uf4cN88YQIjmJxBbKCgMwuMWPQNRE3LwvJ7CX6S4XIlBUydjeKnjay3vikRcEXiVQ2t1m26xQo9YoVbp5yrnM1dWS4XJwNBhTqe1GzW4oebEVlCbfF5iQXy4ek2dCop8QKBgQCkpt51x3Mu0RcvzjsomVqwYIVJmNqE06GSlfJOWNXw/doyuBWi4gj7dMkaAl2UF7a6gZ5FQWNOFA3kwdJWWVU18551vNGNIRS4DScWeCbgvc57y90rJExmOHOgNRURFEaJ3u+9yyzDHYsy2pOvKzArhnP4MF9T9qiXDGhk2pLU4wKBgH9K8kifAOGoomU9gUO/EWFdDxYSSBsZkAE2fdMc5627CLqazCVpLVUQyDrlidLd7CajnbqtM7yKqpbW7M6NG1TAFW7K0VWronWVzXdx0bST9Y/AeCC0WoZWd2aF8DrcH3VO7y+xXm1KHI2JMh2wZDdHoA9tRiSRzXavcQCE98XhAoGARB6tpvjPlKz49NRT8WYQ+SvVuS8+KPoNvMHAr91dFglQfu9eNDdkE/SrRtF3uc9d0CfUPT7iQOlXHi96QDNRZ7LU+k0WPVdUZRk79LXdD9o3VUxwwmqQGTJ90+hiQOCsllnfwhwO9uKANpfrxesOUNOdoy8QIYLJGUUO47rySJUCgYEA9CxCYZcMAAL1wSXhPKobf0nmApywdjtoxmudTk++VZ98bDlz16DOzWeuYr5CuMXqvhK6Ti3PT7IPEjB5f/FjI7ElWKpMNQxls5mzewbLkw43XM+jhiLZ7vQv7nPSt9dX9NtNzuvIpB6JAQtdhaqlhR34D8aJq0Nly4U249qy7l4=" ;
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyQQceVUChTJGtF/a8SXufhSxDTKporieTq9NO7yDZSpDlAX1zVPT/nf0KWAlxq1TYappWMIYtyrOABhJyn6flNP6vuSBiM5lYsepHvYrtRHqlFiJruEkiaCgEZBKL5aCfBHYj0oqgQn9MpNV/PEH4cBYAVaiI4+VX8CBUQfeEGjgN6OkpLULZ3X0JUkmSnVvCNJ1m3PD68IIlbOfEZXJUKCqmZhzprGR5VWswjxA+g87cMwvijL4gdkSy/daG62Bz5vApcmmMkuX1k1fMWP4ajZCASVw8HD+MSLRhd8We9F97gd8CW0TavzbdR+mTS5H4yEgO8F9HRAsbkhV9yu0yQIDAQAB";// 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://ows4dk10ps.52http.net/api/order/pay/alipay/success";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://localhost:10000/pay/success.html";
    // 签名方式
    private  String sign_type = "RSA2";
    // 字符编码格式
    private  String charset = "utf-8";
    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException, UnsupportedEncodingException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = new String(vo.getOut_trade_no().getBytes("UTF-8"),"UTF-8");;
//
//        //付款金额，必填
//        String total_amount = new String(vo.getTotal_amount().getBytes("UTF-8"),"UTF-8");;
//        //订单名称，必填
//        String subject = new String(vo.getSubject().getBytes("UTF-8"),"UTF-8");;
//        //商品描述，可空
//        String body = new String(vo.getBody().getBytes("UTF-8"),"UTF-8");;

        String out_trade_no = vo.getOut_trade_no();
        String total_amount = vo.getTotal_amount();
        String subject = vo.getSubject();
        String body = vo.getBody();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
