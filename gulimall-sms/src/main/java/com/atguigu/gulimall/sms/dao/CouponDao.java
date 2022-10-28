package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券信息
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:17:02
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {

    List<CouponEntity> selectCouponsBySpuID(@Param("spuId") Long spuId);
}
