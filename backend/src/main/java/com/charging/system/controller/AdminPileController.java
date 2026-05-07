package com.charging.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.system.common.Result;
import com.charging.system.entity.ChargingPile;
import com.charging.system.entity.ChargingStation;
import com.charging.system.model.dto.PileQueryDTO;
import com.charging.system.model.vo.ChargingPileVO;
import com.charging.system.service.ChargingPileService;
import com.charging.system.service.ChargingStationService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理端 - 充电桩管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/pile")
public class AdminPileController {

    @Resource
    private ChargingPileService chargingPileService;

    @Resource
    private ChargingStationService chargingStationService;

    /**
     * 1. 分页查询充电桩 (关联查询电站名称)
     */
    @GetMapping("/page")
    public Result<Page<ChargingPileVO>> getPage(PileQueryDTO query) {
        Page<ChargingPile> pageParam = new Page<>(query.getPage(), query.getSize());
        
        LambdaQueryWrapper<ChargingPile> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPileCode()), ChargingPile::getPileCode, query.getPileCode());
        wrapper.eq(query.getStationId() != null, ChargingPile::getStationId, query.getStationId());
        wrapper.eq(query.getStatus() != null, ChargingPile::getStatus, query.getStatus());
        wrapper.orderByDesc(ChargingPile::getCreateTime);

        // 先查出电桩的当前页数据
        chargingPileService.page(pageParam, wrapper);

        Page<ChargingPileVO> voPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), pageParam.getTotal());
        if (pageParam.getRecords().isEmpty()) {
            return Result.success(voPage);
        }

        // 获取当前页中出现的所有站点 ID，并查出名称映射
        List<Long> stationIds = pageParam.getRecords().stream()
                .map(ChargingPile::getStationId)
                .distinct().collect(Collectors.toList());
        Map<Long, String> stationNameMap = chargingStationService.listByIds(stationIds).stream()
                .collect(Collectors.toMap(ChargingStation::getId, ChargingStation::getName));

        // 实体转VO
        List<ChargingPileVO> voRecords = pageParam.getRecords().stream().map(pile -> {
            ChargingPileVO vo = new ChargingPileVO();
            BeanUtils.copyProperties(pile, vo);
            vo.setStationName(stationNameMap.getOrDefault(pile.getStationId(), "未知站点"));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return Result.success("查询成功", voPage);
    }

    /**
     * 2. 新增充电桩
     */
    @PostMapping
    public Result<Boolean> addPile(@RequestBody ChargingPile pile) {
        return Result.success(chargingPileService.save(pile));
    }

    /**
     * 3. 修改充电桩
     */
    @PutMapping
    public Result<Boolean> updatePile(@RequestBody ChargingPile pile) {
        return Result.success(chargingPileService.updateById(pile));
    }

    /**
     * 4. 删除充电桩
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePile(@PathVariable Long id) {
        return Result.success(chargingPileService.removeById(id));
    }
}
