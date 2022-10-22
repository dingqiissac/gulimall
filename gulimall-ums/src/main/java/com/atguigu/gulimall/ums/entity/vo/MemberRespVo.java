package com.atguigu.gulimall.ums.entity.vo;

import lombok.Data;

/**
 * 登录成功以后响应给前端的用户信息
 *
 * 省得以后个人信息还要再查
 */

@Data
public class MemberRespVo {

    private String username;
    private String email;
    private String header;
    private String mobile;
    private String sign;
    private Long levelId;
    //以上部分明文返回。


    //以下部分放在jwt中。
    //前端需要访问的令牌 //使用jwt做的。
    private String token;
}
