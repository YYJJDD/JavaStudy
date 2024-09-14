package com.jingdyang.reflect.service;

import cn.hutool.core.util.StrUtil;
import com.jingdyang.reflect.dao.IndicatorDao;
import com.jingdyang.reflect.entity.IndicatorBO;
import com.jingdyang.reflect.entity.IndicatorDTO;
import com.jingdyang.reflect.enums.DateRangeEnum;
import com.jingdyang.reflect.enums.IndicatorEnum;
import com.jingdyang.reflect.utils.ReflectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicatorService {

    @Resource
    private IndicatorDao indicatorDao;

    /**
     * 由于从Dao层返回的指标为IndicatorBO列表，往IndicatorDTO实体类种的指标属性中set值时，需要根据指标名称找到对应指标值，然后再set，
     * 时间区间枚举{w1, m1, m3, m6, y1, year, total} * 指标枚举{yield, annalYield, maxDrawdown} = 27个指标需要根据名称在indicators
     * 列表中找到对应IndicatorBO，并取值set到IndicatorDTO对应的指标属性，过程繁琐，
     * 故利用反射思想，根据时间区间枚举和指标枚举两两组合得到指标名称，根据反射set到目标实体IndicatorDTO中具体的属性中
     *
     * @param wmpIds
     * @return
     */
    public List<IndicatorDTO> getIndicators(List<String> wmpIds) {
        // 指标枚举列表
        List<IndicatorEnum> indicatorEnumList = Arrays.asList(IndicatorEnum.YIELD, IndicatorEnum.ANNUAL_YIELD,
                IndicatorEnum.MAX_DRAWDOWN);
        // 日期区间枚举
        List<DateRangeEnum> dateRangeEnumList = Arrays.asList(
                DateRangeEnum.W1,
                DateRangeEnum.M1,
                DateRangeEnum.M3,
                DateRangeEnum.M6,
                DateRangeEnum.Y1,
                DateRangeEnum.YEAR,
                DateRangeEnum.TOTAL);
        // 指标信息列表
        List<IndicatorBO> indicators = indicatorDao.getIndicators(wmpIds, indicatorEnumList, dateRangeEnumList);

        // mock test data
        mockTestData(indicators);

        // <理财产品id，产品对应指标信息>
        Map<String/*wmpId*/, List<IndicatorBO>> wmpIdToIndicatorMapping =
                indicators.stream().collect(Collectors.groupingBy(IndicatorBO::getWmpId));

        List<IndicatorDTO> result = new ArrayList<>();
        wmpIdToIndicatorMapping.forEach((wmpId, indicatorBOList) -> {
            IndicatorDTO indicatorDTO = new IndicatorDTO();
            indicatorDTO.setWmpId(wmpId);
            setPropertyValue(indicatorBOList, indicatorDTO);
            result.add(indicatorDTO);
        });

        return result;
    }

    /**
     * 根据反射为target实体设置对应指标属性
     *
     * @param indicatorBOList 单个理财产品指标列表
     * @param target          目标理财产品指标Dto实体
     */
    private void setPropertyValue(List<IndicatorBO> indicatorBOList,
                                  IndicatorDTO target) {
        indicatorBOList.forEach(item -> {
            StringBuilder propertyName = new StringBuilder()
                    .append(item.getDateRange())
                    .append(item.getIndicator().substring(0, 1).toUpperCase())
                    // 下划线转驼峰
                    .append(StrUtil.toCamelCase(item.getIndicator()).substring(1));
            BigDecimal indicatorValue = item.getIndicatorValue();
            ReflectUtils.invokeSetMethod(target, propertyName.toString(), indicatorValue);
        });
    }

    private void mockTestData(List<IndicatorBO> indicators) {
        // 收益率：yield
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("m1")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("m3")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("m6")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("y1")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("y3")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("year")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("yield")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("total")
                .build());

        // 最大回撤：maxDrawdown
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("m1")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("m3")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("m6")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("y1")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("y3")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("year")
                .build());
        indicators.add(IndicatorBO.builder()
                .wmpId("600036")
                .indicator("maxDrawdown")
                .indicatorValue(new BigDecimal("6.6"))
                .dateRange("total")
                .build());
    }

}
