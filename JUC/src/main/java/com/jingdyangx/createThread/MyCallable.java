package com.jingdyangx.createThread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class MyCallable implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("Hello World: " + Thread.currentThread().getName());
        return 42; // 返回一个结果
    }

    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        Thread thread = new Thread(futureTask, "My Callable");
        thread.start(); // 启动线程
        try {
            int result = futureTask.get(); // 获取结果
            System.out.println("Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}