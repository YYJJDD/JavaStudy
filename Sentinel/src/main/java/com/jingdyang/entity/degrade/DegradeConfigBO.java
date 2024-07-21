package com.jingdyang.entity.degrade;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DegradeConfigBO {
    /**
     * 慢调用比例模式下为慢调用临界RT(超出该值为慢点用)；
     * 异常比例 / 异常数模式下为对应的阈值
     */
    private String count;

    /**
     * 熔断时长，单位为s
     */
    private Integer timeWindow;

    /**
     * 慢调用比例
     */
    private Double slowRadio;

    /**
     * 熔断出发的最小请求数，请求数小于该值时即使
     * 异常比率超出阈值也不会熔断(默认值5)
     */
    private Integer minRequestAmount;

    /**
     * 统计时长
     */
    private Integer startInterval;
}
