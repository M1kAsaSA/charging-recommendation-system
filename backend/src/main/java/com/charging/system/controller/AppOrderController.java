package com.charging.system.controller;

import com.charging.system.common.Result;
import com.charging.system.model.dto.StartChargeDTO;
import com.charging.system.service.ChargingOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 微信小程序端 API - 扫码主链路
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/app/order")
public class AppOrderController {

    @Resource
    private ChargingOrderService chargingOrderService;

    /**
     * F-USER-05 扫码发起命令联动硬件开启
     */
    @PostMapping("/scan")
    public Result<String> scanCodeAndStart(@RequestBody @Validated StartChargeDTO dto) {
        log.info("APP端请求扫码桩号启动, 参数:{}", dto);
        // FIXME: 对接鉴权获取的ID，此处强设为合法用户 1001 演示完整订单交易
        Long mockUserId = 1001L;

        try {
            String newOrderNo = chargingOrderService.scanAndStart(mockUserId, dto);
            return Result.success("启动成功，请插入枪头充电", newOrderNo);
        } catch (Exception e) {
            log.error("微信下发设备失败原因：{}", e.getMessage());
            return Result.error(500, e.getMessage());
        }
    }
}
