package com.charging.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.system.entity.ChargingOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电订单 Mapper 接口
 */
@Mapper
public interface ChargingOrderMapper extends BaseMapper<ChargingOrder> {
}
