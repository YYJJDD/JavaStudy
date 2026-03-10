package com.jingdyangx.ProducerConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        
        // 生产者线程
        new Thread(() -> {
            try {
                int i = 0;
                while (true) {
                    queue.put(i++);
                    System.out.println("Producing: " + i);
                    Thread.sleep(1000); // 模拟生产时间
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 消费者线程
        new Thread(() -> {
            try {
                while (true) {
                    int i = queue.take();
                    System.out.println("Consuming: " + i);
                    Thread.sleep(2000); // 模拟消费时间
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}