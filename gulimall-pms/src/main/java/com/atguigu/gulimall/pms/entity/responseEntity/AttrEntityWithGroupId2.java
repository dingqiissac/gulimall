package com.atguigu.gulimall.pms.entity.responseEntity;

import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import lombok.Data;

@Data
public class AttrEntityWithGroupId2 extends AttrEntity {

    private AttrGroupEntity group;
}
