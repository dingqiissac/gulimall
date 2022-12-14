package com.atguigu.gulimall.ums.controller;

import java.util.Arrays;

import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.ums.entity.vo.MemberLogInVo;
import com.atguigu.gulimall.ums.entity.vo.MemberRegister;
import com.atguigu.gulimall.ums.entity.vo.MemberRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.ums.service.MemberService;


/**
 * 会员
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:22:54
 */
@Api(tags = "会员 管理")
@RestController
@RequestMapping("ums/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @ApiOperation("登入操作")
    @PostMapping("/login")
    public Resp<Object> logIn(MemberLogInVo memberLogInVo) {
        try {
            MemberRespVo res = memberService.logIn(memberLogInVo);
            return Resp.ok(res);
        } catch (Exception e) {
            Resp resp = new Resp(1,e.getMessage(),null);
            return resp;
        }

    }


    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Resp<Object> register(MemberRegister memberRegister) {
        try {
            memberService.registerUser(memberRegister);
            return Resp.ok(null);
        } catch (Exception e) {
            Resp resp = new Resp();
            resp.setMsg(e.getMessage());
            resp.setCode(1);
            return resp;
        }

    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:member:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = memberService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:member:info')")
    public Resp<MemberEntity> info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return Resp.ok(member);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:member:save')")
    public Resp<Object> save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:member:update')")
    public Resp<Object> update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:member:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
