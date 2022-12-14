package com.atguigu.gulimall.pms.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.dao.SpuInfoDescDao;
import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.pms.entity.requestEntity.SpuAllSave;
import com.atguigu.gulimall.pms.entity.requestEntity.UpdateBatch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.pms.service.SpuInfoService;




/**
 * spu信息
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2019-08-01 15:52:32
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    @ApiOperation("商品上架、下架")
    @GetMapping("/updateStatus/{spuId}")
    public Resp<Boolean> updateStatus(@PathVariable(value = "spuId") Integer spuId,
                                     @RequestParam(value = "status") Integer status) {
        spuInfoService.updateStatusBySpuId(spuId,status);
        return Resp.fail(null);
    }

    @ApiOperation("批量上架、下架商品")
    @PostMapping("/batch/updateStatus")
    public Resp<Boolean> updateStatusByBatch(@RequestBody UpdateBatch updateBatch) {
        Boolean OK = spuInfoService.updateStatusByBatch(updateBatch);

        if(OK==true){
            return Resp.ok(true);
        }

        return Resp.fail(false);
    }




    @ApiOperation("按照spuid,spuname,分类id检索商品")
    @GetMapping("/simple/search")
    @PreAuthorize("hasAuthority('pms:spuinfo:list')")
    public Resp<PageVo> simpleSearch(QueryCondition queryCondition,
                                     @RequestParam(value = "catId",defaultValue = "0") Integer catId) {
        PageVo page = spuInfoService.queryPageByCatId(queryCondition,catId);

        return Resp.ok(page);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:spuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = spuInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('pms:spuinfo:info')")
    public Resp<SpuInfoEntity> info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return Resp.ok(spuInfo);
    }

    /**
     * 保存
     */
    @Autowired
    SpuInfoDescDao spuInfoDescDao;
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:spuinfo:save')")
    public Resp<Object> save(@RequestBody SpuAllSave spuInfo){
		spuInfoService.spuBigSaveAll(spuInfo);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:spuinfo:update')")
    public Resp<Object> update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:spuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
