package com.jingdyang.common.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 错误码
 */
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {

    SUC0000("0", ""),
    FOC8000("1", "请求外部失败"),

    ;

    private String code;

    private String errorMessage;


}
