package com.jingdyang.service;

import com.google.common.collect.Lists;
import com.jingdyang.dao.ScopePublicFundDao;
import com.jingdyang.dao.YjtFundDao;
import com.jingdyang.entity.YieldResponseBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@Slf4j
public class ScheduleTask {

    @Resource
    ScopePublicFundDao scopePublicFundDao;

    @Resource
    YjtFundDao yjtFundDao;

    /**
     * 以固定频率调度任务（以毫秒为单位）
     * 注意，定时任务默认情况下不会并行运行。因此，即使使用了 fixedRate，在前一个任务完成之前也不会调用下一个任务。
     * 如果想在定时任务中支持并行行为，就需要添加 @Async 注解：
     * 即使前一项任务尚未完成，这项异步任务也会每5min被调用一次。
     */
    @Async
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void updateFundYieldInfo() {
        try {
            List<String> funds = new ArrayList<>(scopePublicFundDao.getScopePublicFund("MARKET_WIDE").keySet());
            List<List<String>> fundParts = Lists.partition(funds, 50);
            List<YieldResponseBO> yieldResponseBOList = yjtFundDao.batchGetYieldInfo(funds.size(), fundParts);
            yieldResponseBOList.forEach(yieldResponseBO -> yjtFundDao.saveYieldById(yieldResponseBO));
            if (funds.size() * 0.8 > yieldResponseBOList.size()) {
                // throw new ServiceException("主机返回收益率数量小于全市场公募基金数量的80%")
            }
        } catch (Exception e) {

        }
        log.info("定时任务-更新银基通收益率接口");
    }

}
