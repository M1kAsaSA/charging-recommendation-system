CREATE DATABASE IF NOT EXISTS `charging_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `charging_system`;

-- 1. 用户表
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `openid` varchar(100) DEFAULT NULL COMMENT '微信openid',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态 0:禁用 1:正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(2) DEFAULT '0' COMMENT '逻辑删除 0:正常 1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 充电站表
CREATE TABLE `charging_station` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '电站名称',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `power_capacity` int(11) DEFAULT '0' COMMENT '总功率(kW)',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态 0:停运 1:运营中',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电站表';

-- 3. 充电桩表
CREATE TABLE `charging_pile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `station_id` bigint(20) NOT NULL COMMENT '所属充电站ID',
  `pile_code` varchar(50) NOT NULL COMMENT '充电桩编号',
  `type` tinyint(4) DEFAULT '1' COMMENT '类型 1:慢充 2:快充',
  `power` int(11) DEFAULT '0' COMMENT '额定功率(kW)',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态 0:空闲 1:充电中 2:故障 3:离线',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pile_code` (`pile_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电桩表';

-- 4. 充电订单表
CREATE TABLE `charging_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `pile_id` bigint(20) NOT NULL COMMENT '充电桩ID',
  `start_time` datetime DEFAULT NULL COMMENT '充电开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '充电结束时间',
  `total_power` decimal(8,2) DEFAULT '0.00' COMMENT '充电总电量(度)',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额(元)',
  `pay_status` tinyint(4) DEFAULT '0' COMMENT '支付状态 0:未支付 1:已支付 2:已退款',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电订单表';

-- 5. 收藏表
CREATE TABLE `favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `station_id` bigint(20) NOT NULL COMMENT '充电站ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_station` (`user_id`,`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 6. 监控日志表
CREATE TABLE `monitor_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `server_ip` varchar(50) DEFAULT NULL COMMENT '服务器IP',
  `cpu_usage` decimal(5,2) DEFAULT NULL COMMENT 'CPU使用率(%)',
  `memory_usage` decimal(5,2) DEFAULT NULL COMMENT '内存使用率(%)',
  `disk_usage` decimal(5,2) DEFAULT NULL COMMENT '磁盘使用率(%)',
  `remark` varchar(255) DEFAULT NULL COMMENT '告警备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控日志表';
