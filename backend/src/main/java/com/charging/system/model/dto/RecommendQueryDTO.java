package com.charging.system.model.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 获取充电站推荐的入参
 */
@Data
public class RecommendQueryDTO {
    
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;
    
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;
    
    // 搜索半径，默认5公里
    private Double radius = 5.0;
}
