package com.charging.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private String openid;
    private Integer status; // 0:禁用 1:正常

    private Date createTime;
    private Date updateTime;
    
    @TableLogic
    private Integer deleted; // 逻辑删除 0:正常 1:删除
}
