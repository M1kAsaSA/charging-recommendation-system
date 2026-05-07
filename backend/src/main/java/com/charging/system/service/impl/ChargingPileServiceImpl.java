package com.charging.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.system.entity.ChargingPile;
import com.charging.system.mapper.ChargingPileMapper;
import com.charging.system.service.ChargingPileService;
import org.springframework.stereotype.Service;

@Service
public class ChargingPileServiceImpl extends ServiceImpl<ChargingPileMapper, ChargingPile> implements ChargingPileService {
}
