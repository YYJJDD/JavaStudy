package com.jingdyang.aop.customAnnotation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    /**
     * 6位基金代码
     */
    private String fundId;

    /**
     * 4位购买平台代码
     */
    private String platformCode;

    /**
     * 持仓金额
     */
    private String amount;

    /**
     * 持仓份额
     */
    private String share;

    /**
     * 基金名称
     */
    private String fundName;
}
