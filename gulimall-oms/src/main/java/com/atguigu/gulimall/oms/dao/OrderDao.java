package com.atguigu.gulimall.oms.dao;

import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:11:29
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateStatusByOrderNum(@Param("orderNum") String orderNum, @Param("status")Long status);
}
