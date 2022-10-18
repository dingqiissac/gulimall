package com.atguigu.gulimall.pms.controller.api;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.entity.requestEntity.CategoryWithChildrenVo;
import com.atguigu.gulimall.pms.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/index")
@RestController
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @ApiOperation("获取所有1级分类")
    @GetMapping("/cates")
    public Resp<List<CategoryEntity>> level1CateLogs(){
        List<CategoryEntity> categoryByLevelAndShowStatus =
                categoryService.getCategoryByLevel(0);

        return Resp.ok(categoryByLevelAndShowStatus);
    }


    @ApiOperation("获取所有1级分类下的子分类")
    @GetMapping("/cates/{id}")
    public Resp<List<CategoryWithChildrenVo>> cateLogsUnderLevel1(@PathVariable(value = "id") Long id){
        List<CategoryWithChildrenVo> categoryByLevelAndShowStatus =
                categoryService.getCategoryAndThirdLevelCateByParentId(id);

        return Resp.ok(categoryByLevelAndShowStatus);
    }



}
