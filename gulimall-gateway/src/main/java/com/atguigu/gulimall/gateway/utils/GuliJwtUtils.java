package com.atguigu.gulimall.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class GuliJwtUtils {

    /**
     * 秘钥....
     */
    private static String key = "DSJAKLDJALJ_ATGUIGU";
    private static String beareaPrefix = "Bearer ";

    /**
     *
     * @param payload  自定义的负载内容
     * @param claims   jwt默认支持的属性
     * @return
     */
    public static String buildJwt(Map<String,Object> payload, Claims claims){

        JwtBuilder builder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, GuliJwtUtils.key)
                .setClaims(payload);//设置自定义的负载

        if(claims!=null){
            if(claims.getId()!=null){
                builder.setId(claims.getId());
            }
            if(claims.getAudience()!=null){
                builder.setAudience(claims.getAudience());
            }
            if(claims.getExpiration()!=null){
                builder.setExpiration(claims.getExpiration());
            }
            if (claims.getNotBefore()!=null){
                builder.setNotBefore(claims.getNotBefore());
            }
            //xxxxx
        }

        String compact = builder.compact();
        return beareaPrefix+ compact;
        //Bearer dsadasdada
    }


    public static void checkJwt(String jwt){

        //去掉Bearer 前缀
        String substring = jwt.substring(beareaPrefix.length(),jwt.length());

        //解出来的串才是jwt
        Jwts.parser().setSigningKey(key).parse(substring);


    }

    public static Map<String,Object> getJwtBody(String jwt){
        String substring = jwt.substring(beareaPrefix.length());

        return Jwts.parser().setSigningKey(key).parseClaimsJws(substring).getBody();
    }

}
