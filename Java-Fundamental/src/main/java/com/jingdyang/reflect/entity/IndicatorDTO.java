package com.jingdyang.reflect.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorDTO {
    /**
     * 产品代码
     */
    private String wmpId;

    /**
     * 近1周收益率
     */
    private BigDecimal w1Yield;

    /**
     * 近1月收益率
     */
    private BigDecimal m1Yield;

    /**
     * 近3月收益率
     */
    private BigDecimal m3Yield;

    /**
     * 近半年收益率
     */
    private BigDecimal m6Yield;

    /**
     * 近1年收益率
     */
    private BigDecimal y1Yield;

    /**
     * 今年以来收益率
     */
    private BigDecimal yearYield;

    /**
     * 成立以来收益率
     */
    private BigDecimal totalYield;

    /**
     * 近1周年化收益率
     */
    private BigDecimal w1AnnualYield;

    /**
     * 近1月年化收益率
     */
    private BigDecimal m1AnnualYield;

    /**
     * 近3月年化收益率
     */
    private BigDecimal m3AnnualYield;

    /**
     * 近半年年化收益率
     */
    private BigDecimal m6AnnualYield;

    /**
     * 近1年年化收益率
     */
    private BigDecimal y1AnnualYield;

    /**
     * 今年以来年化收益率
     */
    private BigDecimal yearAnnualYield;

    /**
     * 成立以来年化收益率
     */
    private BigDecimal totalAnnualYield;


    /**
     * 近1周最大回撤
     */
    private BigDecimal w1MaxDrawdown;

    /**
     * 近1月最大回撤
     */
    private BigDecimal m1MaxDrawdown;

    /**
     * 近3月最大回撤
     */
    private BigDecimal m3MaxDrawdown;

    /**
     * 近半年最大回撤
     */
    private BigDecimal m6MaxDrawdown;

    /**
     * 近1年最大回撤
     */
    private BigDecimal y1MaxDrawdown;

    /**
     * 今年以来最大回撤
     */
    private BigDecimal yearMaxDrawdown;

    /**
     * 成立以来最大回撤
     */
    private BigDecimal totalMaxDrawdown;

    // 其余指标省略，以上述指标举例说明

}
