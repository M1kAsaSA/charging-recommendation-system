package com.charging.system.model.dto;

import lombok.Data;

/**
 * 后台订单查询 DTO
 */
@Data
public class OrderQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    
    private String orderNo;   // 订单号模糊查
    private Long pileId;      // 根据机器ID或桩号找
    private Long stationId;   // 根据站点大区拉取
    private Integer payStatus;// 支付状态
    private String phone;     // 手机号绑定下的单子
}
