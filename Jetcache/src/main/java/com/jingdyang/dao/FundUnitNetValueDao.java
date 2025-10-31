package com.jingdyang.dao;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.jingdyang.consts.CacheNameConst;
import com.jingdyang.dao.mapper.IndicatorDegradeMapper;
import com.jingdyang.dao.mapper.IndicatorMapper;
import com.jingdyang.entity.FundUnitNetValueDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FundUnitNetValueDao {

    // mysql主库（和IndicatorDegradeMapper逻辑完全一致，对应不同数据源）
    @Resource
    IndicatorMapper indicatorMapper;

    // mysql备库（和IndicatorMapper逻辑完全一致，对应不同数据源）
    @Resource
    IndicatorDegradeMapper indicatorDegradeMapper;

    /**
     * 读取基金最新净值数据
     * 优先读Redis，降级Mysql
     *
     * @return 基金代码：净值
     */
    @SentinelResource(value = "readFundUnitNetValue",
            fallback = "getFundUnitNetValueFallBack",
            blockHandler = "getFundUnitNetValueFallBack")
    @Cached(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.FUND_UNIT_NET_VALUE,
            key = "#netValue",
            expire = 3800,
            cacheType = CacheType.LOCAL)
    @CacheRefresh(refresh = 1800)
    public Map<String, BigDecimal> getFundUnitNetValue(String netValue) {
        Map<String, BigDecimal> result = new HashMap<>();
        try {
            // 读mysql主库
            result = indicatorMapper.getFundUnitValueNetValue().stream()
                    .collect(Collectors.toMap(FundUnitNetValueDO::getFundId, FundUnitNetValueDO::getNetValue));
        } catch (Exception e) {
            log.error("Failed to get fund net value from MySQL", e);
            // 打完日志后，一定要手动把异常抛出，否则Sentinel降级不起作用
            throw e;
        }
        return result;
    }

    /**
     * 读取基金最新净值数据
     * 降级方案
     */
    public Map<String, BigDecimal> getFundUnitNetValueFallBack() {
        log.warn("FundUnitNetValue Degrade happened!");
        Map<String, BigDecimal> result = new HashMap<>();
        try {
            // 读mysql备库
            result = indicatorMapper.getFundUnitValueNetValue().stream()
                    .collect(Collectors.toMap(FundUnitNetValueDO::getFundId, FundUnitNetValueDO::getNetValue));
        } catch (Exception e) {
            log.error("Failed to get fund unit net value from MySQL", e);
            throw e;
        }
        return result;
    }
}
