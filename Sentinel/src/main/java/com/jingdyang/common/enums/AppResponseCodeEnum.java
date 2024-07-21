package com.jingdyang.common.enums;

import lombok.Getter;

@Getter
public enum AppResponseCodeEnum {

    SUCCESS("SUCCESS", ""),
    FLOW_LIMIT_EXCEPTION("FAIL001", "系统忙，请稍后重试"),
    AUTHORITY_EXCEPTION("FAIL002", "系统忙，请稍后重试");

    private String code;

    private String message;

    AppResponseCodeEnum(String code, String message) {
    }
}
