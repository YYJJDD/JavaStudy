package com.jingdyang.aop.customAnnotation.enums;

import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@AllArgsConstructor
public enum RangeEnum {

    W1("w1", "近一周"),
    M1("m1", "近一月"),
    M3("m3", "近三月"),
    M6("m6", "近半年"),
    Y1("y1", "近一年"),
    Y3("y3", "近三年"),
    Y5("y5", "近5年"),
    YEAR("year", "今年以来"),
    TOTAL("total", "成立以来");

    private final String code;

    private final String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
