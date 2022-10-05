package com.atguigu.gulimall.wms.dao;

import com.atguigu.gulimall.wms.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:28:19
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
