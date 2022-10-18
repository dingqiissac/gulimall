package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.requestEntity.CategoryWithChildrenVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2019-08-01 15:52:32
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    List<CategoryEntity> getCategoryByLevel(Integer level);

    List<CategoryEntity> getCategoryChildrenById(Integer catId);

    List<CategoryEntity> getCategoryByLevelAndShowStatus(int level, int status);

    List<CategoryWithChildrenVo> getCategoryAndThirdLevelCateByParentId(Long id);

}

