package com.atguigu.gulimall.oms.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.oms.vo.CartVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.service.OrderService;


/**
 * 订单
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:11:29
 */
@Api(tags = "订单 管理")
@RestController
@RequestMapping("oms/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/paySuccess")
    public Resp<String> paySuccess(@RequestParam("orderNum") String orderNum) {

        orderService.updateStatusByOrderNum(orderNum,1L);

        return Resp.ok("OK");
    }

    @PostMapping("/deleteOrder")
    public Resp<String> deleteOrderAndItems(@RequestParam("num") String num) {

        orderService.deleteOrderAndItems(num);


        return Resp.ok("ok");
    }





    @PostMapping("/createAndSave")
    public Resp<OrderEntity> createAndSaveOrder(@RequestBody OrderSubmitVo orderSubmitVo) {

        OrderEntity orderEntity = orderService.createAndSaveOrder(orderSubmitVo);


        return Resp.ok(orderEntity);
    }


    @PostMapping("/findByOrderNum")
    public Resp<OrderEntity> findByOrderNum(@RequestParam("orderToken") String orderToken) {

        OrderEntity orderEntity = orderService.findByOrderNum(orderToken);


        return Resp.ok(orderEntity);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('oms:order:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = orderService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('oms:order:info')")
    public Resp<OrderEntity> info(@PathVariable("id") Long id) {
        OrderEntity order = orderService.getById(id);

        return Resp.ok(order);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('oms:order:save')")
    public Resp<Object> save(@RequestBody OrderEntity order) {
        orderService.save(order);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('oms:order:update')")
    public Resp<Object> update(@RequestBody OrderEntity order) {
        orderService.updateById(order);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('oms:order:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids) {
        orderService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
