package com.jingdyang.reflect.dao;

import com.jingdyang.reflect.entity.IndicatorBO;
import com.jingdyang.reflect.enums.DateRangeEnum;
import com.jingdyang.reflect.enums.IndicatorEnum;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class IndicatorDao {
    /**
     * 根据理财产品ID列表，获取理财产品指标信息
     *
     * @param wmpIds         字符串列表表示多个产品代码
     * @param indicatorEnums 指标枚举
     * @param rangeEnums     日期范围枚举
     * @return 理财产品指标信息详情
     */
    public List<IndicatorBO> getIndicators(List<String> wmpIds,
                                           List<IndicatorEnum> indicatorEnums,
                                           List<DateRangeEnum> rangeEnums) {
        List<IndicatorBO> result = new ArrayList<>();
        // 数据获取逻辑
        return result;
    }
}
