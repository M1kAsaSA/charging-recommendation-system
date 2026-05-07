package com.charging.system.model.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 带有推荐分数的充电站聚合视图对象
 */
@Data
public class StationRecommendVO {
    private Long id;
    private String name;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    
    // 空闲桩数量统计
    private Integer idleFastPileCount;
    private Integer idleSlowPileCount;
    private Integer totalPileCount;
    
    // 实时电价（元/度），演示暂固定
    private Double currentPrice;
    
    // 与用户距离（公里）
    private Double distance;
    
    // 综合推荐得分
    private Double recommendScore;
}
