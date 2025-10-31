package com.jingdyang.dao;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.jingdyang.consts.CacheNameConst;
import com.jingdyang.dao.mapper.IndicatorDegradeMapper;
import com.jingdyang.dao.mapper.IndicatorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class ScopePublicFundDao {

    // mysql主库（和IndicatorDegradeMapper逻辑完全一致，对应不同数据源）
    @Resource
    IndicatorMapper indicatorMapper;

    // mysql备库（和IndicatorMapper逻辑完全一致，对应不同数据源）
    @Resource
    IndicatorDegradeMapper indicatorDegradeMapper;

    /**
     * 读取全市场公募基金数据
     * 优先读Redis，降级Mysql
     *
     * @param scope
     * @return 基金代码：基金名称
     */
    @SentinelResource(value = "readMarketWidePublicFund",
            fallback = "getMarketWidePublicFundFallBack",
            blockHandler = "getMarketWidePublicFundFallBack")
    @Cached(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.MARKET_WIDE_PUBLIC_FUND,
            key = "#scope",
            expire = 3800,
            cacheType = CacheType.LOCAL)
    @CacheRefresh(refresh = 1800)
    public Map<String, String> getScopePublicFund(String scope) {
        Map<String, String> result = new HashMap<>();
        try {
            // 读mysql主库
            indicatorMapper.getScopePublicFund(scope);
        } catch (Exception e) {
            log.error("Failed to get {} public fund from MySQL", scope, e);
            // 打完日志后，一定要手动把异常抛出，否则Sentinel降级不起作用
            throw e;
        }
        return result;
    }

    /**
     * 读取全市场公募基金数据
     * 降级方案
     */
    public Map<String, String> getMarketWidePublicFundFallBack(String scope) {
        log.warn("MarketWidePublicFund Degrade happened!");
        Map<String, String> result = new HashMap<>();
        try {
            // 读mysql备库
            indicatorDegradeMapper.getScopePublicFund(scope);
        } catch (Exception e) {
            log.error("Failed to get {} public fund from MySQL", scope, e);
            throw e;
        }
        return result;
    }
}
