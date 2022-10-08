package com.atguigu.gulimall.pms.entity.requestEntity;

import lombok.Data;

import java.util.List;

@Data
public class UpdateBatch {

    private int status;

    private List<Integer> spuIds;
}
