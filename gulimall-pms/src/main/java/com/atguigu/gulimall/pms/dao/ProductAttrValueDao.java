package com.atguigu.gulimall.pms.dao;

import com.atguigu.gulimall.pms.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * spu属性值
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2019-08-01 15:52:32
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    void insertBatch(@Param("baseAttrs") ArrayList<ProductAttrValueEntity> productAttrValueEntities);
}
