package com.atguigu.gulimall.wms.service.impl;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.wms.vo.LockStockVo;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.wms.dao.WareSkuDao;
import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    @Qualifier("mainThreadPool")
    ThreadPoolExecutor executor;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<WareSkuEntity> queryStockBySkuId(Long skuId) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        wareSkuEntityQueryWrapper.eq("sku_id", skuId);

        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(wareSkuEntityQueryWrapper);

        return wareSkuEntities;
    }

    @Override
    public List<WareSkuEntity> queryStockBySkuIds(List<Long> skuIds) {
        List<WareSkuEntity> skuStocks = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds));
        return skuStocks;
    }

    @Transactional
    @Override
    public LockStockVo lockAndCheckStock(List<SkuLockVo> skuIds) {
        // distributed LOCK for every skuId
        LockStockVo lockStockVo = new LockStockVo();
        List<SkuLock> skuLocks = new ArrayList<>();

        CompletableFuture[] completableFutures = new CompletableFuture[skuIds.size()];

        AtomicReference<Boolean> flag = new AtomicReference<>(true);


        for (int i = 0; i < skuIds.size(); i++) {
            String token = skuIds.get(0).getOrderToken();
            SkuLockVo skuLockVo = skuIds.get(i);
            String finalToken = token;
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                SkuLock skuLock = null;
                try {
                    skuLock = lockSku(skuLockVo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                skuLock.setOrderToken(finalToken);
                skuLocks.add(skuLock);
                if (skuLock.getSuccess() == false) {
                    flag.set(false);
                }
            }, executor);

            completableFutures[i] = voidCompletableFuture;
        }


        try {
            CompletableFuture.allOf(completableFutures).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        lockStockVo.setLocks(skuLocks);
        lockStockVo.setLocked(flag.get());


        if (flag.get()) {
            //都锁住了 就发消息给mq
            rabbitTemplate.convertAndSend(com.atguigu.gulimall.wms.config.Constant.SKU_ID_EXCHANGE,
                    com.atguigu.gulimall.wms.config.Constant.TO_DELAYED_QUEUE_ROUTE_KEY, skuLocks);
        }


        return lockStockVo;
    }

    private SkuLock lockSku(SkuLockVo skuLockVo) throws InterruptedException {
        //
        SkuLock skuLock = new SkuLock();
        RLock lock = redissonClient.getLock(Constant.STOCK_LOCK + skuLockVo.getSkuId());
        boolean b = lock.tryLock(1, 1, TimeUnit.MINUTES);

        if (!b) {
            skuLock.setSuccess(false);
            return skuLock;
        }

        // SELECT * FROM `wms_ware_sku` WHERE sku_id = #{skuId.skuId}
        // and stock-stock_locked>=#{skuId.num}

        try{
            List<WareSkuEntity> availableStock = wareSkuDao.checkStock(skuLockVo);
            if (availableStock != null && availableStock.size() > 0) {
                WareSkuEntity wareSkuEntity = availableStock.get(0);
                // UPDATE `wms_ware_sku` SET stock_locked = stock_locked + #{num}
                // WHERE ware_id =#{wareSkuEntity.wareId} AND sku_id = #{wareSkuEntity.skuId}
                Long success = wareSkuDao.lockStockBySkuIdAndWareId(wareSkuEntity, skuLockVo.getNum());
                if (success > 0) {
                    skuLock.setSuccess(true);
                    skuLock.setLocked(skuLockVo.getNum());
                    skuLock.setSkuId(skuLockVo.getSkuId());
                    skuLock.setWareId(wareSkuEntity.getWareId());
                }

            } else {
                skuLock.setSkuId(skuLockVo.getSkuId());
                skuLock.setLocked(0);
                skuLock.setSuccess(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return skuLock;
        }finally {
            lock.unlock();
        }
        return skuLock;
    }

}