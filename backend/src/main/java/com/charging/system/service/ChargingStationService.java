package com.charging.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.system.entity.ChargingStation;
import com.charging.system.model.dto.RecommendQueryDTO;
import com.charging.system.model.vo.StationRecommendVO;

import java.util.List;

/**
 * 充电站服务类
 */
public interface ChargingStationService extends IService<ChargingStation> {

    /**
     * 基于多因子协同过滤（距离、空闲、电价、偏好）推荐周边的充电站
     *
     * @param query 经纬度及查询参数
     * @param userId 当前用户ID，用于历史偏好加权（预留扩展）
     * @return 排序后的推荐充电站集合
     */
    List<StationRecommendVO> getRecommendStations(RecommendQueryDTO query, Long userId);
}
