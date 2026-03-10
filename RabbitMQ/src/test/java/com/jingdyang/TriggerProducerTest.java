package com.jingdyang;


import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class TriggerProducerTest {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.listener.simple.exchange}")
    private String insuranceExchange;

    @Test
    public void sendTriggerMessage(){
        // 测试用例
        rabbitTemplate.convertAndSend(insuranceExchange, "", "{\n" +
                "    \"fromDate\": null,\n" +
                "    \"toDate\": null,\n" +
                "    \"subTaskIds\": null,\n" +
                "    \"taskId\": \"CMB_INSURANCE_SUB_ACCOUNT_NET_VALUE_TASK\"\n" +
                "}");
    }
}

