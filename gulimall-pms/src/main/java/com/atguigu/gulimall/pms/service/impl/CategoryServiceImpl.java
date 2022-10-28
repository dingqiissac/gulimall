package com.atguigu.gulimall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.pms.entity.requestEntity.CategoryWithChildrenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.CategoryDao;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<CategoryEntity> getCategoryByLevel(Integer level) {
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        switch (level) {
            case 0:
                return redisAndDB(categoryEntities, level, Constant.CACHE_CATELOG);
            case 1:
                return redisAndDB(categoryEntities, level, Constant.CACHE_CATELOG1);
            case 2:
                return redisAndDB(categoryEntities, level, Constant.CACHE_CATELOG2);
            case 3:
                return redisAndDB(categoryEntities, level, Constant.CACHE_CATELOG3);
        }
        return categoryEntities;
    }

    public List<CategoryEntity> redisAndDB(List<CategoryEntity> categoryEntities, Integer level, String con) {
        //先读redis
        String res = redisTemplate.opsForValue().get(con);
        if (res != null) {
            categoryEntities = JSON.parseArray(res, CategoryEntity.class);
            redisTemplate.opsForValue().increment("inc");
            return categoryEntities;
        }
        //如果redis 没有就读取数据库
        //写之前加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(Constant.LOCK, "lock", 4, TimeUnit.MINUTES);
        if (lock) {
            List<CategoryEntity> results = getResults(level);
            String redisValue = JSON.toJSONString(results);
            Random random = new Random();
            redisTemplate.opsForValue().set(con, redisValue, random.nextInt(10)+1,TimeUnit.MINUTES);
            redisTemplate.delete(Constant.LOCK);
            return results;
        } else {
            getCategoryByLevel(level);
        }
        return categoryEntities;
    }


    private List<CategoryEntity> getResults(Integer level) {
        List<CategoryEntity> categoryEntities = null;

        QueryWrapper queryWrapper = new QueryWrapper<CategoryEntity>();
        if (level != 0) {
            queryWrapper.eq("cat_level", level);
        }
        categoryEntities = categoryDao.selectList(queryWrapper);

        return categoryEntities;
    }


    @Override
    public List<CategoryEntity> getCategoryChildrenById(Integer catId) {
        QueryWrapper queryWrapper = new QueryWrapper<CategoryEntity>();

        queryWrapper.eq("parent_cid", catId);

        List<CategoryEntity> categoryEntities = categoryDao.selectList(queryWrapper);

        return categoryEntities;
    }

    @Override
    public List<CategoryEntity> getCategoryByLevelAndShowStatus(int level, int status) {
        List<CategoryEntity> categoryEntities = categoryDao.selectList(new QueryWrapper<CategoryEntity>()
                .eq("cat_level", level).eq("show_status", status));

        return categoryEntities;
    }

    @Override
    public List<CategoryWithChildrenVo> getCategoryAndThirdLevelCateByParentId(Long id) {

        List<CategoryWithChildrenVo> res = categoryDao.selectCateChildren(id);

        return res;
    }


}