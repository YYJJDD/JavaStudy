package com.jingdyang.trigger;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务接口
 */
public interface Task {

    /**
     * 单个任务ID
     */
    String getId();

    /**
     * 多个任务ID
     */
    default String[] getIds() {
        return new String[0];
    }

    /**
     * 更新当日全部子任务
     */
    default void dailyUpdateAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * 更新当日部分子任务
     *
     * @param subTaskIds 子任务列表
     */
    default void dailyUpdate(List<String> subTaskIds) {
        throw new UnsupportedOperationException();
    }

    /**
     * 更新历史全部子任务
     *
     * @param toDate   结束日期
     * @param fromDate 起始日期
     */
    default void historyUpdateAll(LocalDate fromDate, LocalDate toDate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 更新历史部分子任务
     *
     * @param subTaskIds 子任务ID列表
     * @param toDate     结束日期
     * @param fromDate   起始日期
     */
    default void historyUpdate(List<String> subTaskIds, LocalDate fromDate, LocalDate toDate) {
        throw new UnsupportedOperationException();
    }

}
