package com.charging.system.model.dto;

import lombok.Data;

/**
 * 后台电桩查询DTO
 */
@Data
public class PileQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String pileCode; // 桩编号模糊查询
    private Long stationId; // 归属电站ID
    private Integer status; // 桩状态
}
