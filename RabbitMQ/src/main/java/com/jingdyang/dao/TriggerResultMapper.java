package com.jingdyang.dao;

import com.jingdyang.entity.TriggerTaskDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

/**
 * 记录触发任务执行状态信息Mapper
 */
@Mapper
public interface TriggerResultMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("insert into cmb_service_trigger_result(message,message_id,status) " +
            "values(#{message},#{messageId},#{status})")
    long startTriggerTask(TriggerTaskDo triggerTaskDo);

    @Update("update cmb_service_trigger_result set status=#{status}, " +
            "begin_time=#{beginTime} where id=#{id}")
    void updateTriggerResultToRunning(TriggerTaskDo triggerTaskDo);

    @Update("update cmb_service_trigger_result set status=#{status}, " +
            "end_time=#{endTime} where id=#{id}")
    void updateTriggerResultToSuccess(TriggerTaskDo triggerTaskDo);


    @Update("update cmb_service_trigger_result set status=#{status}, " +
            "end_time=#{endTime}, error_stack=#{errorStack} where id=#{id}")
    void updateTriggerResultToError(TriggerTaskDo triggerMessage);
}
