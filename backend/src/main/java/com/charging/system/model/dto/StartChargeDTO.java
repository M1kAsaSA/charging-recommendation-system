package com.charging.system.model.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 扫码发起充电 DTO
 */
@Data
public class StartChargeDTO {
    
    @NotBlank(message = "必须扫实装机器的唯一识别码")
    private String pileCode;
    
}
