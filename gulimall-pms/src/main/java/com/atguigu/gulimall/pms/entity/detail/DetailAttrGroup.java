package com.atguigu.gulimall.pms.entity.detail;

import lombok.Data;

import java.util.List;

@Data
public class DetailAttrGroup {

    private Long id;
    private String name;
    private List<DetailBaseAttrVo> attrs;

}
