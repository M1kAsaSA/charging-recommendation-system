package com.charging.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper (临时被OrderController查阅)
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
