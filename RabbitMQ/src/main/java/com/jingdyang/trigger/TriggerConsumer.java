package com.jingdyang.trigger;

import com.jingdyang.dao.TriggerResultMapper;
import com.jingdyang.entity.TriggerMqMessageDo;
import com.jingdyang.entity.TriggerTaskDo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@RabbitListener(queues = "${spring.rabbitmq.listener.simple.queue:queue.cmb.insurance.task}")
public class TriggerConsumer {

    @Resource
    private TriggerResultMapper triggerResultMapper;

    @Resource
    private MessageTaskDispatchFactory messageTaskDispatchFactory;

    @RabbitHandler
    public void process(String content, Channel channel, Message message) {
        TriggerMqMessageDo triggerMqMessageDo = TriggerMqMessageDo.getInstance(content);
        if (null == triggerMqMessageDo) {
            log.warn("mq 消息异常");
            return;
        }

        TriggerTaskDo triggerTaskDo = TriggerTaskDo.getInstance(content, triggerMqMessageDo.getMessageId());
        triggerResultMapper.startTriggerTask(triggerTaskDo);

        try {
            // 成功消费消息就进行应答，不考虑失败重跑
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            triggerTaskDo.updateToRunning();
            triggerResultMapper.updateTriggerResultToRunning(triggerTaskDo);

            // 任务执行
            MessageTaskDispatch taskDispatch = messageTaskDispatchFactory.getInstance(triggerMqMessageDo.getTaskId());
            taskDispatch.process(triggerMqMessageDo);

            triggerTaskDo.updateToSuccess();
            triggerResultMapper.updateTriggerResultToSuccess(triggerTaskDo);
        } catch (Exception e) {
            log.error("errMsg", e);
            triggerTaskDo.updateToError(e.getMessage());
            triggerResultMapper.updateTriggerResultToError(triggerTaskDo);
        }

    }


}
