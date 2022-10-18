package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResponse;

import java.io.IOException;

public interface SearchService {


    SearchResponse search(SearchParam searchParam);
}
