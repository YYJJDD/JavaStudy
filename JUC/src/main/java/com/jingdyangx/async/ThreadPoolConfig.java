package com.jingdyangx.async;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    /**
     * CPU核数
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    // 核心线程数
    private final int corePoolSize = AVAILABLE_PROCESSORS + 1;
    // 最大线程数
    private final int maxPoolSize = 10;
    // 队列大小
    private final long queueSize = 100;
    // 线程最大空闲时间
    private final long keepAliveSeconds = 100;
    // 阻塞队列
    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(5);


    @Bean(value = "taskThreadPool")
    public ThreadPoolExecutor taskThreadPool() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
