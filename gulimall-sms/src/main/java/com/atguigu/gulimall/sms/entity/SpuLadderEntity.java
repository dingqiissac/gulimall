package com.atguigu.gulimall.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品阶梯价格
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:17:02
 */
@ApiModel
@Data
@TableName("sms_spu_ladder")
public class SpuLadderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty(name = "id",value = "id")
	private Long id;
	/**
	 * spu_id
	 */
	@ApiModelProperty(name = "spuId",value = "spu_id")
	private Long spuId;
	/**
	 * 满几件
	 */
	@ApiModelProperty(name = "fullCount",value = "满几件")
	private Integer fullCount;
	/**
	 * 打几折
	 */
	@ApiModelProperty(name = "discount",value = "打几折")
	private BigDecimal discount;
	/**
	 * 折后价
	 */
	@ApiModelProperty(name = "price",value = "折后价")
	private BigDecimal price;
	/**
	 * 是否叠加其他优惠[0-不可叠加，1-可叠加]
	 */
	@ApiModelProperty(name = "addOther",value = "是否叠加其他优惠[0-不可叠加，1-可叠加]")
	private Integer addOther;

}
