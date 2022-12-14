package com.atguigu.gulimall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 仓库信息
 * 
 * @author leifengyang
 * @email lfy@atguigu.com
 * @date 2022-10-05 17:28:19
 */
@ApiModel
@Data
@TableName("wms_ware_info")
public class WareInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty(name = "id",value = "id")
	private Long id;
	/**
	 * 仓库名
	 */
	@ApiModelProperty(name = "name",value = "仓库名")
	private String name;
	/**
	 * 仓库地址
	 */
	@ApiModelProperty(name = "address",value = "仓库地址")
	private String address;
	/**
	 * 区域编码
	 */
	@ApiModelProperty(name = "areacode",value = "区域编码")
	private String areacode;

}
