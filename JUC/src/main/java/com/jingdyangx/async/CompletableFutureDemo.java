package com.jingdyangx.async;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class CompletableFutureDemo {

    /**
     * CPU核数
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    // 核心线程数
    private static final int corePoolSize = AVAILABLE_PROCESSORS / 2 + 1;
    // 最大线程数
    private static final int maxPoolSize = 10;
    // 队列大小
    private static final long queueSize = 100;
    // 线程最大空闲时间
    private static final long keepAliveSeconds = 100;
    // 阻塞队列
    private static final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);

    /**
     * 自定义消费队列线程池
     * CallerRunsPolicy 这个策略重试添加当前的任务，他会自动重复调用 execute() 方法，直到成功。
     * AbortPolicy 对拒绝任务抛弃处理，并且抛出异常。
     * DiscardPolicy 对拒绝任务直接无声抛弃，没有异常信息。
     * DiscardOldestPolicy 对拒绝任务不抛弃，而是抛弃队列里面等待最久的一个线程，然后把拒绝任务加到队列。
     */
    private static final ThreadPoolExecutor asyncTaskExecutor = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveSeconds,
            TimeUnit.SECONDS,
            queue,
            new ThreadPoolExecutor.CallerRunsPolicy());


    public static void main(String[] args) {
        CompletableFutureDemo completableFutureDemo = new CompletableFutureDemo();
        String result = completableFutureDemo.testOrderTask();
        System.out.println("result:" + result);
    }

    public String testOrderTask() {
        List<CompletableFuture<List<Integer>>> futureList = Lists.newArrayList();

        // 任务1
        CompletableFuture<List<Integer>> task1 = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(1L);
            return Lists.newArrayList(1, 2, 3);
        }, asyncTaskExecutor);
        futureList.add(task1);

        // 任务2
        CompletableFuture<List<Integer>> task2 = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(2L);
            return Lists.newArrayList(4, 5, 6);
        }, asyncTaskExecutor);
        futureList.add(task2);

        // 任务3
        CompletableFuture<List<Integer>> task3 = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(3L);
            return Lists.newArrayList(7, 8, 9);
        }, asyncTaskExecutor);
        futureList.add(task3);

        long l = System.currentTimeMillis();
        // 写法1
        List<Integer> newList = futureList.stream().map(CompletableFuture::join).flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("写法1耗时：" +  (System.currentTimeMillis() - l) + "ms");

        // 写法2
        long l2 = System.currentTimeMillis();
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        List<Integer> asyncResult = getAsyncResult(futureList);
        System.out.println("写法2耗时：" +  (System.currentTimeMillis() - l2) + "ms");

        System.out.println("写法1结果：" + newList);
        System.out.println("写法2结果：" + asyncResult);

        return JSON.toJSONString(asyncResult);
    }

    private void sleepSeconds(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> getAsyncResult(List<CompletableFuture<List<Integer>>> futureList) {
        if (CollectionUtil.isEmpty(futureList)) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        for (CompletableFuture<List<Integer>> future : futureList) {
            try {
                List<Integer> integers = future.get();
                if (ObjectUtils.isEmpty(future)) {
                    continue;
                }
                result.addAll(integers);
            } catch (InterruptedException | ExecutionException e) {
                log.error("异步线程获取结果异常");
                throw new RuntimeException("异步线程获取结果异常", e);
            }
        }
        return result;
    }

}
