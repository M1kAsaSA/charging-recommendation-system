package com.charging.system.model.vo;

import com.charging.system.entity.ChargingOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带聚合的订单数据对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChargingOrderVO extends ChargingOrder {
    private String username;     // 用户归属
    private String phone;        // 支付手机号
    private String pileCode;     // 设备码展示
    private String stationName;  // 为了列表展示关联的电站名
}
