package com.charging.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.system.entity.ChargingOrder;
import com.charging.system.entity.ChargingPile;
import com.charging.system.mapper.ChargingOrderMapper;
import com.charging.system.model.dto.StartChargeDTO;
import com.charging.system.service.ChargingOrderService;
import com.charging.system.service.ChargingPileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ChargingOrderServiceImpl extends ServiceImpl<ChargingOrderMapper, ChargingOrder> implements ChargingOrderService {

    @Resource
    private ChargingPileService chargingPileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String scanAndStart(Long userId, StartChargeDTO dto) {
        log.info("用户[{}] 扫码发起充电, 请求设备号: {}", userId, dto.getPileCode());

        // 1. 获取充电桩信息，核验当前终端真伪
        LambdaQueryWrapper<ChargingPile> pileWrapper = new LambdaQueryWrapper<>();
        pileWrapper.eq(ChargingPile::getPileCode, dto.getPileCode());
        ChargingPile pile = chargingPileService.getOne(pileWrapper);

        if (pile == null) {
            throw new RuntimeException("非法设备异常，扫码无效！");
        }

        // 2. F-USER-05 校验机器工作运转是否正常可用
        if (pile.getStatus() != 0) {
            throw new RuntimeException("该充电桩当前已被占用或故障中，请换一台重试");
        }

        // 3. 修改电桩状态为：运营连入充电中(1)
        pile.setStatus(1);
        chargingPileService.updateById(pile);

        // 4. 创建关联表结构(流水订单据核心落表)
        ChargingOrder order = new ChargingOrder();
        // 自动序列生成带时间戳+设备标志的订单号 (演示为简化的UUID后段)
        String orderNo = "CO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setPileId(pile.getId());
        order.setStartTime(new Date());
        order.setPayStatus(0); // 待支付出账

        this.save(order);
        
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean stopAndSettle(String orderNo) {
        // 1. 获取该流水记录是否存在悬而未解的状态
        LambdaQueryWrapper<ChargingOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(ChargingOrder::getOrderNo, orderNo);
        ChargingOrder order = this.getOne(orderWrapper);

        if (order == null || order.getEndTime() != null) {
            throw new RuntimeException("订单状态不正常，可能已结束计费");
        }

        // 2. 解除设备的锁定：归还闲置桩子(还原0)
        ChargingPile pile = chargingPileService.getById(order.getPileId());
        if (pile != null) {
            pile.setStatus(0);
            chargingPileService.updateById(pile);
        }

        // 3. 结算逻辑：计算电费（演示逻辑：假定电站计费每度电随机浮动，这里作全量统一化计算）
        Date endTime = new Date();
        long timeDiffMills = endTime.getTime() - order.getStartTime().getTime(); // 毫秒
        // 此处方便展示效果，假设1分钟代表实际的1小时度电消耗模型（实际这需要接入硬件通讯的回执电表），现在造数据
        double hoursElasped = (double) timeDiffMills / (1000 * 60); 

        // 模拟基础的每小时 7KW（即一分钟7度电演示）
        BigDecimal totalPower = new BigDecimal(hoursElasped * 7).setScale(2, RoundingMode.HALF_UP);
        // 按每度电 1.25元计费
        BigDecimal totalAmount = totalPower.multiply(new BigDecimal("1.25")).setScale(2, RoundingMode.HALF_UP);

        order.setEndTime(endTime);
        order.setTotalPower(totalPower);
        order.setTotalAmount(totalAmount);
        // order.setPayStatus(0); 依然待支付，等待客户端调出微信支付确认窗付款转 1
        
        return this.updateById(order);
    }
}
