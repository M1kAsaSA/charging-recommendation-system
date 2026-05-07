package com.charging.system.controller;

import com.charging.system.common.Result;
import com.charging.system.model.dto.RecommendQueryDTO;
import com.charging.system.model.vo.StationRecommendVO;
import com.charging.system.service.ChargingStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 充电站推荐前台 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/station")
public class ChargingStationController {

    @Resource
    private ChargingStationService chargingStationService;

    /**
     * F-USER-02 智能推荐算法接口
     * 多因子协同过滤：根据用户经纬度，动态返回周边带权重的推荐列表
     */
    @PostMapping("/recommend")
    public Result<List<StationRecommendVO>> recommend(@RequestBody @Validated RecommendQueryDTO queryDTO) {
        log.info("小程序端请求附近智能充电桩推荐，参数:{}", queryDTO);
        
        // 模拟从 JWT Token 获取的当前登录用户 ID，联调期先写死 1001
        Long currentUserId = 1001L;

        List<StationRecommendVO> recommendations = chargingStationService.getRecommendStations(queryDTO, currentUserId);
        
        return Result.success("智能推荐计算成功", recommendations);
    }
}
