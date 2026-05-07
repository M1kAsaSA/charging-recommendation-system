package com.charging.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.system.entity.ChargingOrder;
import com.charging.system.model.dto.StartChargeDTO;

/**
 * 核心订单结算流转服务
 */
public interface ChargingOrderService extends IService<ChargingOrder> {
    
    /**
     * 扫码发起充电 (用户端)
     * @param userId 当用户
     * @param dto 请求参
     * @return 新生成的OrderNo单号
     */
    String scanAndStart(Long userId, StartChargeDTO dto);
    
    /**
     * 结束并结单扣除 (用户端/后台强行干预端共用)
     * @param orderNo 订单编码
     * @return 是否停止成功
     */
    Boolean stopAndSettle(String orderNo);
}
