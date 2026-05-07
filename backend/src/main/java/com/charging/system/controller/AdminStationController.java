package com.charging.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.system.common.Result;
import com.charging.system.entity.ChargingStation;
import com.charging.system.model.dto.StationQueryDTO;
import com.charging.system.service.ChargingStationService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 后台管理端 - 电站管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/station")
public class AdminStationController {

    @Resource
    private ChargingStationService chargingStationService;

    /**
     * 1. 分页查询充电站列表
     */
    @GetMapping("/page")
    public Result<Page<ChargingStation>> getPage(StationQueryDTO query) {
        Page<ChargingStation> pageParam = new Page<>(query.getPage(), query.getSize());
        
        LambdaQueryWrapper<ChargingStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()), ChargingStation::getName, query.getName());
        wrapper.eq(query.getStatus() != null, ChargingStation::getStatus, query.getStatus());
        wrapper.orderByDesc(ChargingStation::getCreateTime);

        Page<ChargingStation> pageResult = chargingStationService.page(pageParam, wrapper);
        return Result.success("查询成功", pageResult);
    }

    /**
     * 2. 新增充电站
     */
    @PostMapping
    public Result<Boolean> addStation(@RequestBody ChargingStation station) {
        return Result.success(chargingStationService.save(station));
    }

    /**
     * 3. 修改充电站信息
     */
    @PutMapping
    public Result<Boolean> updateStation(@RequestBody ChargingStation station) {
        return Result.success(chargingStationService.updateById(station));
    }

    /**
     * 4. 根据ID查询详情
     */
    @GetMapping("/{id}")
    public Result<ChargingStation> getById(@PathVariable Long id) {
        return Result.success(chargingStationService.getById(id));
    }
    
    /**
     * 5. 删除充电站
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStation(@PathVariable Long id) {
        return Result.success(chargingStationService.removeById(id));
    }

    /**
     * 6. 获取所有电站精简列表（用于下拉框联动）
     */
    @GetMapping("/list")
    public Result<java.util.List<ChargingStation>> listAll() {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChargingStation> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.select(ChargingStation::getId, ChargingStation::getName);
        return Result.success(chargingStationService.list(wrapper));
    }
}
