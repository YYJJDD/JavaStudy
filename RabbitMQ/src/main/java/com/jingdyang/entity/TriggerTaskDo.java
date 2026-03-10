package com.jingdyang.entity;

import com.jingdyang.enums.TriggerTaskStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 记录触发任务执行状态信息实体
 */
@Data
public class TriggerTaskDo {
    private long id;
    private String message;
    private TriggerTaskStatusEnum status;
    private String messageId;
    private String errorStack;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;

    public static TriggerTaskDo getInstance(String message, String messageId) {
        TriggerTaskDo triggerTaskDo = new TriggerTaskDo();
        triggerTaskDo.setMessage(message);
        triggerTaskDo.setMessageId(messageId);
        triggerTaskDo.setStatus(TriggerTaskStatusEnum.START);
        return triggerTaskDo;
    }

    public void updateToRunning() {
        setStatus(TriggerTaskStatusEnum.RUNNING);
        setBeginTime(LocalDateTime.now());
    }

    public void updateToSuccess() {
        setStatus(TriggerTaskStatusEnum.SUCCESS);
        setEndTime(LocalDateTime.now());
    }

    public void updateToError(String errorStack) {
        setStatus(TriggerTaskStatusEnum.ERROR);
        setEndTime(LocalDateTime.now());
        setErrorStack(errorStack);
    }

}
