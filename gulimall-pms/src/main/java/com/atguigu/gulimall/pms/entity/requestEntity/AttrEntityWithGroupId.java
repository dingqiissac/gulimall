package com.atguigu.gulimall.pms.entity.requestEntity;

import com.atguigu.gulimall.pms.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrEntityWithGroupId extends AttrEntity {

    private Long attrGroupId;
}
