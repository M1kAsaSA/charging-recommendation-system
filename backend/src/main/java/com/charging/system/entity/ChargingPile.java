package com.charging.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 充电桩实体类
 */
@Data
@TableName("charging_pile")
public class ChargingPile implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long stationId;
    private String pileCode;
    private Integer type; // 1:慢充 2:快充
    private Integer power;
    private Integer status; // 0:空闲 1:充电中 2:故障 3:离线

    private Date createTime;
    private Date updateTime;
}
