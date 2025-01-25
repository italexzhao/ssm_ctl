package com.ss.ctrai.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    PHONE("PHONE"),
    WECHAT("WECHAT");
    
    private final String value;
    
    AccountType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
} 