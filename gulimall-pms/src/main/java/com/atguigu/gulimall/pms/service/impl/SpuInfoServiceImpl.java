package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.entity.requestEntity.UpdateBatch;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.SpuInfoDao;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDao spuInfoDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Integer catId) {
        //select * from table catlogid = 277 and (spu_name like ss or id = ss)
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<SpuInfoEntity>();
        if(catId != 0){
            spuInfoEntityQueryWrapper.eq("catalog_id",catId);
        }
        if(!StringUtils.isEmpty(queryCondition.getKey())){
            spuInfoEntityQueryWrapper.and(obj ->{
                obj.like("spu_name",queryCondition.getKey());
                obj.or().like("id",queryCondition.getKey());
                return obj;
            });
        }


        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);

        IPage<SpuInfoEntity> spuInfoEntityIPage = this.page(page, spuInfoEntityQueryWrapper);

        return new PageVo(spuInfoEntityIPage);
    }

    @Override
    public Boolean updateStatusBySpuId(Integer spuId, Integer status) {
      //update table set status = '0' where id = spuId
        UpdateWrapper<SpuInfoEntity> updateWrapper = new UpdateWrapper<SpuInfoEntity>();

        updateWrapper.set("publish_status",status).eq("id",spuId);

        int update = spuInfoDao.update(null, updateWrapper);

        return update>0 ? true:false;
    }

    @Override
    public Boolean updateStatusByBatch(UpdateBatch updateBatch) {
        UpdateWrapper<SpuInfoEntity> spuInfoEntityUpdateWrapper = new UpdateWrapper<>();
        spuInfoEntityUpdateWrapper.lambda()
                .in(SpuInfoEntity::getId,updateBatch.getSpuIds())
                .set(SpuInfoEntity::getPublishStatus,updateBatch.getStatus());
        int update = spuInfoDao.update(null, spuInfoEntityUpdateWrapper);

        return update>0?true:false;
    }

}