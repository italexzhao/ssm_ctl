package com.ss.ctrai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.ctrai.enums.AccountType;
import java.util.List;
import lombok.Data;

@Data
public class AccountSegment {
    @JsonProperty("prefix")
    private String prefix;      // 手机号前段或完整微信号
    @JsonProperty("suffix")
    private String suffix;      // 手机号后段
    @JsonProperty("type")
    private AccountType type;
    @JsonProperty("wechatNumber")
    private String wechatNumber; // 关联的微信号
    @JsonProperty("numList")
    private List<String> numList; // 分段的手机号列表，例如：["138****0000", "13800000000"]
} 