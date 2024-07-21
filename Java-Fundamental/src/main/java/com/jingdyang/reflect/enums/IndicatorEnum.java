package com.jingdyang.reflect.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IndicatorEnum {
    /**
     * 收益率
     */
    YIELD("yield"),
    /**
     * 年化收益率
     */
    ANNUAL_YIELD("annual_yield"),
    /**
     * 最大回撤
     */
    MAX_DRAWDOWN("max_drawdown"),
    /**
     * 夏普比率
     */
    SHARPE_RATIO("sharpe"),
    /**
     * 卡尔玛比率
     */
    CALMAR_RATIO("calmar"),
    /**
     * 动态回撤
     */
    DYNAMIC_DRAWDOWN("dynamic_drawdown"),
    /**
     * 持有正收益率
     */
    HOLD_POSITIVE_YIELD("hold_positive_yield");

    private final String value;
}
