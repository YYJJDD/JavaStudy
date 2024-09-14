package com.jingdyang.reflect.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicatorBO {
    /**
     * 理财产品id
     */
    private String wmpId;
    /**
     * 指标名称（详见IndicatorEnum枚举）
     */
    private String indicator;
    /**
     * 时间区间（详见IndicatorEnum枚举）
     */
    private String dateRange;
    /**
     * 数据更新日期
     */
    private LocalDate latestDate;
    /**
     * 指标值
     */
    private BigDecimal indicatorValue;
}
