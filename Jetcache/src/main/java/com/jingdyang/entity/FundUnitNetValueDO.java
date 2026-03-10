package com.jingdyang.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundUnitNetValueDO {
    private String fundId;
    @JSONField(name = "net_value")
    private BigDecimal netValue;
}
