package com.charging.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.system.common.Result;
import com.charging.system.entity.ChargingOrder;
import com.charging.system.entity.ChargingPile;
import com.charging.system.entity.ChargingStation;
import com.charging.system.model.vo.DashboardStatsVO;
import com.charging.system.service.ChargingOrderService;
import com.charging.system.service.ChargingPileService;
import com.charging.system.service.ChargingStationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 后台管理端 - 首页数据监控大盘
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    @Resource
    private ChargingStationService chargingStationService;

    @Resource
    private ChargingPileService chargingPileService;

    @Resource
    private ChargingOrderService chargingOrderService;

    @GetMapping("/stats")
    public Result<DashboardStatsVO> getDashboardStats() {
        DashboardStatsVO statsVO = new DashboardStatsVO();
        
        // 1. 站点总数
        statsVO.setTotalStations(chargingStationService.count());
        
        // 2. 电桩相关统计
        statsVO.setTotalPiles(chargingPileService.count());
        statsVO.setIdlePiles(chargingPileService.count(new LambdaQueryWrapper<ChargingPile>().eq(ChargingPile::getStatus, 0)));
        statsVO.setChargingPiles(chargingPileService.count(new LambdaQueryWrapper<ChargingPile>().eq(ChargingPile::getStatus, 1)));
        statsVO.setFaultPiles(chargingPileService.count(new LambdaQueryWrapper<ChargingPile>().in(ChargingPile::getStatus, 2, 3)));

        // 3. 订单与收入统计
        List<ChargingOrder> orders = chargingOrderService.list(new LambdaQueryWrapper<ChargingOrder>().eq(ChargingOrder::getPayStatus, 1)); // 1是已支付
        statsVO.setTotalOrders(orders.size());
        
        BigDecimal revenue = orders.stream()
                .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statsVO.setTotalRevenue(revenue);

        return Result.success("获取大盘数据成功", statsVO);
    }
}