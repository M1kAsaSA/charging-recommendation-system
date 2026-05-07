package com.charging.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充电站实体类
 */
@Data
@TableName("charging_station")
public class ChargingStation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer powerCapacity;
    private Integer status; // 0:停运 1:运营中

    private Date createTime;
    private Date updateTime;
}
