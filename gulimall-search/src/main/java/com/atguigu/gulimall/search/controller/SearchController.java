package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @ApiOperation("前端发送请求 收集Es—data")
    @GetMapping("/")
    public Resp<Object> search(SearchParam param){

        searchService.search(param);

        return null;
    }
}
