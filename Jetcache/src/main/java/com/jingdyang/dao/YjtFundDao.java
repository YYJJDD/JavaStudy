package com.jingdyang.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.google.common.collect.Lists;
import com.jingdyang.consts.CacheNameConst;
import com.jingdyang.entity.YieldResponseBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 请求主机银基通接口Dao
 */

@Repository
@Slf4j
public class YjtFundDao {

    private final Executor bizTaskExecutor;

    public YjtFundDao(Executor bizTaskExecutor) {
        this.bizTaskExecutor = bizTaskExecutor;
    }

    public Map<String, YieldResponseBO> getFundYieldInfo(List<String> fundIdList) {
        Map<String, YieldResponseBO> resultMap = new HashMap<>();
        List<String> searchList = new ArrayList<>();
        // 查内存缓存，当缓存无对应结果时，进行实时http查询
        fundIdList.forEach(fundId -> {
            YieldResponseBO yieldResponseBO = getYieldById(fundId);
            if (Objects.nonNull(yieldResponseBO)) {
                resultMap.put(fundId, yieldResponseBO);
            } else {
                searchList.add(fundId);
            }
        });
        // 查询http，并异步存入缓存
        if (!ObjectUtils.isEmpty(searchList)) {
            Map<String, YieldResponseBO> searchResults = getRealYieldInfo(searchList);
            if (Objects.nonNull(searchResults)){
                resultMap.putAll(searchResults);
                asyncSetLocalYieldMap(searchResults);
            }
        }
        return resultMap;
    }

    @Cached(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.YJT_FUND_YIELD,
            key = "#fundId",
            expire = 3800,
            localLimit = 20000,
            cacheType = CacheType.LOCAL)
    public YieldResponseBO getYieldById(String fundId) {
        return null;
    }

    // @CacheUpdate：缓存更新，value为缓存更新后的值。此操作是调用原方法结束后将更新缓存
    @CacheUpdate(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.YJT_FUND_YIELD,
            key = "#yieldResponseBO.zFndCod",
            value = "#yieldResponseBO")
    public void saveYieldById(YieldResponseBO yieldResponseBO) {
    }

    /**
     * 本地缓存查不到的基金进行实时请求主机接口，对此方法进行记录
     * @param fundIdList
     * @return
     */
    public Map<String, YieldResponseBO> getRealYieldInfo(List<String> fundIdList) {
        List<List<String>> fundIdListParts = Lists.partition(fundIdList, 100);
        List<YieldResponseBO> yieldResponseBOList;
        if (fundIdList.size() > 1) {
            yieldResponseBOList = batchGetYieldInfo(fundIdList.size(), fundIdListParts);
        } else {
            yieldResponseBOList = doGetYieldInfo(fundIdList);
        }
        if (ObjectUtils.isEmpty(yieldResponseBOList)) {
            log.error("本地缓存查不到的基金请求http异常：" + String.join(",", fundIdList));
            return null;
        }
        return yieldResponseBOList.stream()
                .collect(Collectors.toMap(YieldResponseBO::getZFndCod, v -> v, (k1, k2) -> k1));
    }


    /**
     * 多线程分批查询http请求
     *
     * @param size
     * @param fundIdListParts
     * @return
     */
    public List<YieldResponseBO> batchGetYieldInfo(int size, List<List<String>> fundIdListParts) {
        List<YieldResponseBO> yieldBOS = Lists.newArrayListWithCapacity(size);
        CompletableFuture[] futures = fundIdListParts.stream().map(fundIdListPart -> {
            try {
                return CompletableFuture.supplyAsync(() -> doGetYieldInfo(fundIdListPart), bizTaskExecutor);
            } catch (Exception e) {
                return CompletableFuture.completedFuture(null);
            }
        }).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        for (CompletableFuture completableFuture : futures) {
            try {
                Object futureObj = completableFuture.get(2000, TimeUnit.MICROSECONDS);
                if (Objects.nonNull(futureObj)) {
                    yieldBOS.addAll((List<YieldResponseBO>) futureObj);
                }
            } catch (Exception e) {
                log.error("batchGetYieldInfo completableFuture get error:{}", e.getMessage());
            }
        }
        return yieldBOS;
    }

    private List<YieldResponseBO> doGetYieldInfo(List<String> fundIdList) {
        List<YieldResponseBO> result = new ArrayList<>();
        // 递归请求上游续传接口
        return result;
    }


    /**
     * 异步将http请求到的（并且本地map没有）数据，更新到本地map中
     */
    private void asyncSetLocalYieldMap(Map<String, YieldResponseBO> searchResults){
        CompletableFuture.runAsync(()->{
            for (Map.Entry<String, YieldResponseBO> entry : searchResults.entrySet()) {
                saveYieldById(entry.getValue());
            }
        }, bizTaskExecutor);
    }

}
