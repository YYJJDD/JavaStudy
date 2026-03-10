package com.jingdyang.trigger;


import com.jingdyang.entity.TriggerMqMessageDo;

import java.time.LocalDate;
import java.util.List;

public interface MessageTaskDispatch {

    /**
     * forward to dailyUpdateAll()
     */
    void dailyUpdate();

    /**
     * forward to dailyUpdateAll(<String> subTaskIds)
     *
     * @param subTaskIds 子任务id（产品id）
     */
    void dailyUpdate(List<String> subTaskIds);

    /**
     * forward to historyUpdateAll(LocalDate fromDate, LocalDate toDate)
     *
     * @param fromDate 起始日期
     * @param toDate   结束日期
     */
    void historyUpdateAll(LocalDate fromDate, LocalDate toDate);

    /**
     * forward to historyUpdate(List<String> subTaskIds, LocalDate fromDate, LocalDate toDate)
     *
     * @param subTaskIds
     * @param fromDate
     * @param toDate
     */
    void historyUpdate(List<String> subTaskIds, LocalDate fromDate, LocalDate toDate);

    void process(TriggerMqMessageDo message);

}
