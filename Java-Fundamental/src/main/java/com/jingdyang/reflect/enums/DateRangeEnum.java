package com.jingdyang.reflect.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum DateRangeEnum {

    W1("w1", "近一周"),
    M1("m1", "近一月"),
    M3("m3", "近三月"),
    M6("m6", "近半年"),
    Y1("y1", "近一年"),
    Y3("y3", "近三年"),
    Y5("y5", "近5年"),
    YEAR("year", "今年以来"),
    TOTAL("total", "成立以来");

    private final String value;

    private final String description;
}
