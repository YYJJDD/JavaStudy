package com.jingdyang.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    // queue.cmb.insurance.task
    @Value("${spring.rabbitmq.listener.simple.queue}")
    private String insuranceQueue;

    // exchange.cmb.insurance.task
    @Value("${spring.rabbitmq.listener.simple.exchange}")
    private String insuranceExchange;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return factory;
    }

    @Bean
    public Queue basicDataTaskDataQueue() {
        Map<String, Object> mqConfig = new HashMap<>();
        return new Queue(insuranceQueue, false, false, false, mqConfig);
    }

    @Bean
    FanoutExchange basicDataTaskExchange(){
        return new FanoutExchange(insuranceExchange);
    }

    @Bean
    Binding bindingExchangeMessages(Queue basicDataTaskQueue, FanoutExchange basicDataTaskExchange) {
        return BindingBuilder.bind(basicDataTaskQueue).to(basicDataTaskExchange);
    }

}
