package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "gulimall-ums")
public interface UmsFeignService {

    @GetMapping("ums/memberreceiveaddress/member/{id}")
    public Resp<List<MemberAddressVo>> memberAddress(@PathVariable("id") Long memberId);
}
