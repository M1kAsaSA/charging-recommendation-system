package com.charging.system.model.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardStatsVO {
    // 基础统计
    private long totalStations;
    private long totalPiles;
    private long totalOrders;
    private BigDecimal totalRevenue;

    // 状态统计 (图表数据)
    private long idlePiles;
    private long chargingPiles;
    private long faultPiles;
}