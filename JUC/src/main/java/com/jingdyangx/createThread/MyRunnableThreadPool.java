package com.jingdyangx.createThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 总结
 *
 *     继承 Thread 类:
 *         优点：简单直观，可以直接访问和设置线程属性。
 *         缺点：Java 中类只能单继承，所以不能同时继承其他类。
 *
 *     实现 Runnable 接口:
 *         优点：可以与其他类继承关系共存，更加灵活。
 *         缺点：不能直接访问线程对象。
 *
 *     使用 Callable 和 FutureTask:
 *         优点：支持返回值和异常处理。
 *         缺点：相比 Runnable 接口，代码稍微复杂一些。
 *
 *     使用 ExecutorService:
 *         优点：可以管理线程池，提高资源利用率。
 *         缺点：需要额外的配置和管理。
 *
 * 以上是创建 Java 线程的四种常见方式及其示例代码。你可以根据实际需求选择最合适的方式来创建和管理线程。
 */
class MyRunnableThreadPool implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello World: " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new MyRunnableThreadPool()); // 提交任务给线程池
        executorService.shutdown(); // 关闭线程池
    }
}