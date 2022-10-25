package com.atguigu.gulimall.ums.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.*;
import com.atguigu.gulimall.commons.exception.EmailDuplicatedException;
import com.atguigu.gulimall.commons.exception.MobileDuplicatedException;
import com.atguigu.gulimall.commons.exception.PassWordAndUserNameInvlidationException;
import com.atguigu.gulimall.commons.exception.UserDuplicatedException;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.ums.entity.vo.MemberLogInVo;
import com.atguigu.gulimall.ums.entity.vo.MemberRegister;
import com.atguigu.gulimall.ums.entity.vo.MemberRespVo;
import com.sun.deploy.security.UserDeclinedException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gulimall.ums.dao.MemberDao;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void registerUser(MemberRegister memberRegister) throws RuntimeException {
        MemberEntity user = new MemberEntity();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(memberRegister.getPassWord());

        user.setUsername(memberRegister.getUserName());
        user.setEmail(memberRegister.getEmail());
        user.setMobile(memberRegister.getPhone());
        user.setPassword(encode);

        if (this.count(new QueryWrapper<MemberEntity>().eq("username", memberRegister.getUserName())) > 0) {
            throw new UserDuplicatedException();
        }


        if (this.count(new QueryWrapper<MemberEntity>().eq("mobile", memberRegister.getPhone())) > 0) {
            throw new MobileDuplicatedException();
        }


        if (this.count(new QueryWrapper<MemberEntity>().eq("email", memberRegister.getEmail())) > 0) {
            throw new EmailDuplicatedException();
        }

        this.save(user);
    }

    @Override
    public MemberRespVo logIn(MemberLogInVo memberLogInVo) throws PassWordAndUserNameInvlidationException {


        MemberEntity user = this.getOne(new QueryWrapper<MemberEntity>().or().eq("username", memberLogInVo.getLogInAcct())
                .or().eq("mobile", memberLogInVo.getLogInAcct()).or().eq("email", memberLogInVo.getLogInAcct()));

        if (user == null) {
            throw new PassWordAndUserNameInvlidationException();
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(memberLogInVo.getPassWord(), user.getPassword());

        if (matches) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            stringRedisTemplate.opsForValue().set
                    (Constant.LOGIN_USER_PREFIX+uuid, JSON.toJSONString(user),Constant.LOGIN_TIME_OFF, TimeUnit.MINUTES);

            Map<String,Object> map = new HashMap<>();
            map.put("token",uuid);
            map.put("id",user.getId());
            String jwt = GuliJwtUtils.buildJwt(map, null);

            MemberRespVo memberRespVo = new MemberRespVo();
            memberRespVo.setEmail(user.getEmail());
            memberRespVo.setHeader(user.getHeader());
            memberRespVo.setLevelId(user.getLevelId());
            memberRespVo.setMobile(user.getMobile());
            memberRespVo.setUsername(user.getUsername());
            memberRespVo.setToken(jwt);

            return memberRespVo;
        } else {
            throw new PassWordAndUserNameInvlidationException();
        }

    }

}