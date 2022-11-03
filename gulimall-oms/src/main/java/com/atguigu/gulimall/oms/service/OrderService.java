package com.atguigu.gulimall.oms.service;

import com.atguigu.gulimall.oms.vo.CartVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 订单
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:11:29
 */
public interface OrderService extends IService<OrderEntity> {

    PageVo queryPage(QueryCondition params);

    OrderEntity createAndSaveOrder(OrderSubmitVo cartVo);

    OrderEntity findByOrderNum(String orderToken);
}

