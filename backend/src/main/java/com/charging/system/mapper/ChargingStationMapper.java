package com.charging.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.system.entity.ChargingStation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电站 Mapper 接口
 */
@Mapper
public interface ChargingStationMapper extends BaseMapper<ChargingStation> {
}
