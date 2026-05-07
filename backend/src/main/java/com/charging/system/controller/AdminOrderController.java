package com.charging.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.system.common.Result;
import com.charging.system.entity.ChargingOrder;
import com.charging.system.entity.ChargingPile;
import com.charging.system.entity.ChargingStation;
import com.charging.system.entity.User;
import com.charging.system.model.dto.OrderQueryDTO;
import com.charging.system.model.vo.ChargingOrderVO;
import com.charging.system.service.ChargingOrderService;
import com.charging.system.service.ChargingPileService;
import com.charging.system.service.ChargingStationService;
import com.charging.system.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理端 - 订单资金大盘监控
 */
@RestController
@RequestMapping("/api/v1/admin/order")
public class AdminOrderController {

    @Resource
    private ChargingOrderService chargingOrderService;

    @Resource
    private ChargingPileService chargingPileService;

    @Resource
    private ChargingStationService chargingStationService;

    @Resource
    private UserMapper userMapper; // 为了简单演示拉取Phone不再写独立Service

    /**
     * 1. 监控全量流水（F-ADMIN-03）多表聚合查询，回显站/桩/用户信息
     */
    @GetMapping("/page")
    public Result<Page<ChargingOrderVO>> getOrderPage(OrderQueryDTO query) {
        Page<ChargingOrder> pageParam = new Page<>(query.getPage(), query.getSize());
        
        LambdaQueryWrapper<ChargingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getOrderNo()), ChargingOrder::getOrderNo, query.getOrderNo());
        wrapper.eq(query.getPayStatus() != null, ChargingOrder::getPayStatus, query.getPayStatus());
        wrapper.orderByDesc(ChargingOrder::getCreateTime);

        // TODO: 如果传递 Phone (即用户搜索单子)，此处要在 user 先检索 ID 后关联到 IN查询，篇幅原因省略
        
        Page<ChargingOrder> orderPage = chargingOrderService.page(pageParam, wrapper);

        Page<ChargingOrderVO> voPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), pageParam.getTotal());
        if (orderPage.getRecords().isEmpty()) {
            return Result.success(voPage);
        }

        // 大表关联补聚合字典信息
        List<Long> userIds = orderPage.getRecords().stream().map(ChargingOrder::getUserId).distinct().collect(Collectors.toList());
        List<Long> pileIds = orderPage.getRecords().stream().map(ChargingOrder::getPileId).distinct().collect(Collectors.toList());
        
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, ChargingPile> pileMap = chargingPileService.listByIds(pileIds).stream().collect(Collectors.toMap(ChargingPile::getId, p -> p));
        
        // 查找站信息
        List<Long> stationIds = pileMap.values().stream().map(ChargingPile::getStationId).distinct().collect(Collectors.toList());
        Map<Long, String> stationNameMap = chargingStationService.listByIds(stationIds).stream().collect(Collectors.toMap(ChargingStation::getId, ChargingStation::getName));

        // 流式组装
        List<ChargingOrderVO> voList = orderPage.getRecords().stream().map(order -> {
            ChargingOrderVO vo = new ChargingOrderVO();
            BeanUtils.copyProperties(order, vo);
            
            ChargingPile rp = pileMap.get(order.getPileId());
            if (rp != null) {
                vo.setPileCode(rp.getPileCode());
                vo.setStationName(stationNameMap.get(rp.getStationId()));
            }

            User ru = userMap.get(order.getUserId());
            if (ru != null) {
                vo.setUsername(ru.getUsername());
                vo.setPhone(ru.getPhone());
            } else {
                vo.setUsername("游客微信用户");
                vo.setPhone("138xxxx****");
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return Result.success("大盘拉取成功", voPage);
    }
    
    /**
     * 2. 安全干预与紧急断电结单结算 (F-ADMIN-04)
     */
     @PutMapping("/forceFinish/{orderNo}")
     public Result<Boolean> forceFinishRemote(@PathVariable String orderNo){
         try {
             return Result.success("断电释放该桩结算通过", chargingOrderService.stopAndSettle(orderNo));
         } catch (Exception e){
             return Result.error(500, e.getMessage());
         }
     }
}
