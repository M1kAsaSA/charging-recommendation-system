package com.charging.system.model.dto;

import lombok.Data;

/**
 * 充电站后台分页查询条件
 */
@Data
public class StationQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String name;    // 根据名称模糊查
    private Integer status; // 运营状态
}
