package com.atguigu.gulimall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.sms.entity.HomeSubjectSpuEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * δΈι’εε
 *
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:17:02
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageVo queryPage(QueryCondition params);
}

