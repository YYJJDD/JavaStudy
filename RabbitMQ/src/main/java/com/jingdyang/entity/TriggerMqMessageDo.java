package com.jingdyang.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * RabbitMQ消息实体
 */
@Data
public class TriggerMqMessageDo {

    private String messageId;
    private String taskId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<String> subTaskIds;

    public static TriggerMqMessageDo getInstance(String message) {
        return JSONObject.parseObject(message, TriggerMqMessageDo.class);
    }

}
