package com.atguigu.gulimall.pms.entity.requestEntity;

import com.atguigu.gulimall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

@Data
public class CategoryWithChildrenVo extends CategoryEntity {

    List<CategoryEntity> subs;

}
