package com.jingdyang.trigger;

import com.jingdyang.entity.TriggerMqMessageDo;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MessageTaskDispatchImpl implements MessageTaskDispatch {

    private final Task task;

    public MessageTaskDispatchImpl(Task task) {
        this.task = task;
    }

    @Override
    public void dailyUpdate() {
        task.dailyUpdateAll();
    }

    @Override
    public void dailyUpdate(List<String> subTaskIds) {
        task.dailyUpdate(subTaskIds);
    }

    @Override
    public void historyUpdateAll(LocalDate fromDate, LocalDate toDate) {
        Objects.requireNonNull(fromDate, "fromDate cannot be null");
        Objects.requireNonNull(toDate, "toDate cannot be null");

        task.historyUpdateAll(fromDate, toDate);
    }

    @Override
    public void historyUpdate(List<String> subTaskIds, LocalDate fromDate, LocalDate toDate) {
        Objects.requireNonNull(fromDate, "fromDate cannot be null");
        Objects.requireNonNull(toDate, "toDate cannot be null");

        task.historyUpdate(subTaskIds, fromDate, toDate);
    }

    @Override
    public void process(TriggerMqMessageDo triggerMqMessageDo) {
        boolean existsFromDate = Objects.nonNull(triggerMqMessageDo.getFromDate());
        boolean existsToDate = Objects.nonNull(triggerMqMessageDo.getToDate());
        boolean existsSubTask = Objects.nonNull(triggerMqMessageDo.getSubTaskIds());

        if (existsFromDate && existsToDate && existsSubTask) {
            log.info(String.format("messageId=%s, process by historyUpdate(SubTaskIds, FromDate, ToDate)",
                    triggerMqMessageDo.getMessageId()));
            historyUpdate(triggerMqMessageDo.getSubTaskIds(), triggerMqMessageDo.getFromDate(),
                    triggerMqMessageDo.getToDate());
        } else if (existsFromDate && existsToDate) {
            log.info(String.format("messageId=%s, process by historyUpdateAll(FromDate, ToDate)",
                    triggerMqMessageDo.getMessageId()));
            historyUpdateAll(triggerMqMessageDo.getFromDate(),
                    triggerMqMessageDo.getToDate());
        } else if (existsSubTask) {
            log.info(String.format("messageId=%s, process by dailyUpdate()",
                    triggerMqMessageDo.getMessageId()));
            dailyUpdate(triggerMqMessageDo.getSubTaskIds());
        } else {
            log.info(String.format("messageId=%s, process by dailyUpdateAll()",
                    triggerMqMessageDo.getMessageId()));
            dailyUpdate();
        }
    }
}
