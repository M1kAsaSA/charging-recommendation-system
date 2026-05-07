package com.charging.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.system.entity.ChargingPile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电桩 Mapper 接口
 */
@Mapper
public interface ChargingPileMapper extends BaseMapper<ChargingPile> {
}
