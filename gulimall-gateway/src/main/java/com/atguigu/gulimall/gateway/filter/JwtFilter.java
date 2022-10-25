package com.atguigu.gulimall.gateway.filter;

import com.atguigu.gulimall.gateway.bean.Constant;
import com.atguigu.gulimall.gateway.utils.GuliJwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class JwtFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate redisTemplate;

    //全局filter，检查令牌。
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authorization = request.getHeaders().get("Authorization");
        if (authorization != null && authorization.size() > 0) {
            String jwt = authorization.get(0);
            try {
                GuliJwtUtils.checkJwt(jwt);
                //redis extent data
                Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(jwt);
                String token = (String) jwtBody.get("token");
                String key = Constant.LOGIN_USER_PREFIX + token;
                redisTemplate.expire(key, Constant.LOGIN_TIME_OFF, TimeUnit.MINUTES);
                return chain.filter(exchange);
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }

        }
        return chain.filter(exchange);
    }
}
