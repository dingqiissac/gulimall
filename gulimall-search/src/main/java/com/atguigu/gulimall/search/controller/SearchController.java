package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/api/search")
@RestController
public class SearchController {

    @Autowired
    SearchService searchService;

    @ApiOperation("前端发送请求 收集Es—data")
    @GetMapping("/")
    public SearchResponse search(SearchParam param){

        SearchResponse search = searchService.search(param);

        return search;
    }
}
