package com.atguigu.gulimall.wms.vo;


import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import java.util.List;

/**
 * 库存锁定的信息
 */
@Data
public class LockStockVo {

    private List<SkuLock> locks;

    //拆单逻辑；订单里面有很多的商品来源于不同的仓库，以仓库发货为单位进行拆分
    private Boolean locked;//最终锁定成功还是失败。
}


