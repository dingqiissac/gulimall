package com.atguigu.gulimall.ums.service;

import com.atguigu.gulimall.ums.entity.vo.MemberLogInVo;
import com.atguigu.gulimall.ums.entity.vo.MemberRegister;
import com.atguigu.gulimall.ums.entity.vo.MemberRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 会员
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:22:54
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

    void registerUser(MemberRegister memberRegister) throws RuntimeException;

    MemberRespVo logIn(MemberLogInVo memberLogInVo);

}

