package com.atguigu.gulimall.commons.to;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品库存
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:28:19
 */

@Data
public class WareSkuVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */

	private Long id;
	/**
	 * sku_id
	 */

	private Long skuId;
	/**
	 * 仓库id
	 */

	private Long wareId;
	/**
	 * 库存数
	 */

	private Integer stock;
	/**
	 * sku_name
	 */

	private String skuName;
	/**
	 * 锁定库存
	 */

	private Integer stockLocked;

}
