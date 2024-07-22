package com.jingdyang.aop.customAnnotation.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.jingdyang.aop.customAnnotation.annotation.AsDouble;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserPositionDO {

    /**
     * 6位基金代码
     */
    @JSONField(name = "fund_id")
//    @Pattern(regexp = "^\\w(6,8)$", message = "invalid fund id!")
    private String fundId;

    /**
     * 4位购买平台代码
     */
    @JSONField(name = "platform_code")
//    @Length(min = 4, max = 4, message = "invalid platform code!")
    private String platformCode;

    /**
     * 持仓金额
     */
    @AsDouble(min = 0, max = 1e9, message = "invalid amount!")
    private String amount;

    /**
     * 持仓份额
     */
    @Nullable
    private String share;

    /**
     * 基金名称
     */
    private String fundName;
}
