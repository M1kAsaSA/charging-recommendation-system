package com.charging.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.system.entity.ChargingPile;
import com.charging.system.entity.ChargingStation;
import com.charging.system.mapper.ChargingPileMapper;
import com.charging.system.mapper.ChargingStationMapper;
import com.charging.system.model.dto.RecommendQueryDTO;
import com.charging.system.model.vo.StationRecommendVO;
import com.charging.system.service.ChargingStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChargingStationServiceImpl extends ServiceImpl<ChargingStationMapper, ChargingStation> implements ChargingStationService {

    @Resource
    private ChargingPileMapper chargingPileMapper;

    // 推荐因子权重常量 (参考设计文档 3.核心算法)
    private static final double W1 = 0.30; // 距离权重
    private static final double W2 = 0.40; // 空闲率权重
    private static final double W3 = 0.20; // 价格权重
    private static final double W4 = 0.10; // 偏好权重
    private static final double MAX_RADIUS_KM = 10.0; // 假定搜索或评估上限距离为10km
    private static final double MAX_PRICE = 2.0; // 本地最高电价上限，做价格归一化
    
    // 地球平均半径（公里）
    private static final double EARTH_RADIUS = 6371.0;

    @Override
    public List<StationRecommendVO> getRecommendStations(RecommendQueryDTO query, Long userId) {
        log.info("用户ID[{}], 请求多因子充电站推荐，经度:{}, 纬度:{}, 半径:{}", userId, query.getLongitude(), query.getLatitude(), query.getRadius());

        // 1. 获取范围内的站点。在实际生产环境可采用 MySQL ST_Distance 或 Redis Geo，此处作为原型用全查+业务层过滤演示
        LambdaQueryWrapper<ChargingStation> stationWrapper = new LambdaQueryWrapper<>();
        stationWrapper.eq(ChargingStation::getStatus, 1); // 仅查询运营中电站
        List<ChargingStation> allStations = this.list(stationWrapper);

        List<StationRecommendVO> voList = new ArrayList<>();
        
        for (ChargingStation station : allStations) {
            // 计算距离，Haversine半正矢公式
            double distanceKm = calculateDistance(query.getLatitude().doubleValue(), query.getLongitude().doubleValue(),
                    station.getLatitude().doubleValue(), station.getLongitude().doubleValue());
            
            // 剔除不在半径范围内的充电站
            if (distanceKm > query.getRadius()) {
                continue;
            }

            StationRecommendVO vo = new StationRecommendVO();
            BeanUtils.copyProperties(station, vo);
            vo.setDistance(distanceKm);
            
            // 查询空闲桩
            buildPileInfo(vo, station.getId());
            
            // 假数据的实时电价，演示默认1.0-1.5元左右随机
            double currentPrice = 1.0 + Math.random() * 0.5;
            vo.setCurrentPrice(currentPrice);
            
            // 2. 多因子加权模型计算: Score = w1 * D + w2 * I + w3 * P + w4 * Pref
            double score = calculateScore(vo, userId);
            vo.setRecommendScore(score);
            
            voList.add(vo);
        }

        // 3. 排序返回
        return voList.stream()
                .sorted(Comparator.comparing(StationRecommendVO::getRecommendScore).reversed()) // 从高到低
                .collect(Collectors.toList());
    }

    /**
     * 补充该站点下的桩的数量统计
     */
    private void buildPileInfo(StationRecommendVO vo, Long stationId) {
        LambdaQueryWrapper<ChargingPile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingPile::getStationId, stationId);
        List<ChargingPile> piles = chargingPileMapper.selectList(wrapper);

        int idleFast = 0;
        int idleSlow = 0;
        
        for (ChargingPile p : piles) {
            if (p.getStatus() == 0) { // 空闲状态
                if (p.getType() == 2) { // 快充
                    idleFast++;
                } else {
                    idleSlow++;
                }
            }
        }
        vo.setTotalPileCount(piles.size());
        vo.setIdleFastPileCount(idleFast);
        vo.setIdleSlowPileCount(idleSlow);
    }
    
    /**
     * 计算推荐指数分
     */
    private double calculateScore(StationRecommendVO vo, Long userId) {
        // (1) 距离归一化：离得越近，距离得分越高 (0-1)
        double distanceNorm = Math.max(0, 1 - (vo.getDistance() / MAX_RADIUS_KM));
        
        // (2) 空闲率归一化：空闲率越高，得分越高 (0-1)
        double idleNorm = 0.0;
        if (vo.getTotalPileCount() != null && vo.getTotalPileCount() > 0) {
            idleNorm = (double)(vo.getIdleFastPileCount() + vo.getIdleSlowPileCount()) / vo.getTotalPileCount();
        }
        
        // (3) 电价归一化：越偏宜，得分越高 (0-1)
        double priceNorm = Math.max(0, 1 - (vo.getCurrentPrice() / MAX_PRICE));
        
        // (4) 偏好预测：此处可基于协同过滤召回的用户平时订单占比计算，演示暂固定赋基础分，后期结合订单表扩展。
        double prefNorm = 0.5; // 如果有过关注或常去历史，可以达到1.0
        
        // 加权累加 (得分按照百分制乘以 100)
        return (W1 * distanceNorm + W2 * idleNorm + W3 * priceNorm + W4 * prefNorm) * 100;
    }

    // --- Haversine 公式计算实际地理距离 ---
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
