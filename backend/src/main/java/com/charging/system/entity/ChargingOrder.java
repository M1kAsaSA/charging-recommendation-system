package com.charging.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充电订单实体类
 */
@Data
@TableName("charging_order")
public class ChargingOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    private Long userId;
    private Long pileId;
    
    private Date startTime;
    private Date endTime;
    
    private BigDecimal totalPower;
    private BigDecimal totalAmount;
    private Integer payStatus; // 0:未支付 1:已支付 2:已退款

    private Date createTime;
    private Date updateTime;
}
