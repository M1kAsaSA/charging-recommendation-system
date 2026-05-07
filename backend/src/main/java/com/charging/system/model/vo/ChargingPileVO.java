package com.charging.system.model.vo;

import com.charging.system.entity.ChargingPile;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 充电桩聚合视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChargingPileVO extends ChargingPile {
    // 聚合查询出的归属电站名称
    private String stationName;
}
