package com.jingdyangx.scene;

import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerWaitNotify {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> buffer = new ArrayList<>();
        int maxSize = 10;

        // 生产者线程
        Runnable producer = () -> {
            synchronized (buffer) {
                try {
                    int i = 0;
                    while (true) {
                        while (buffer.size() == maxSize) {
                            buffer.wait();
                        }
                        buffer.add(i++);
                        System.out.println("Producing: " + i);
                        buffer.notifyAll();
                        Thread.sleep(500); // 模拟生产时间
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // 消费者线程
        Runnable consumer = () -> {
            synchronized (buffer) {
                try {
                    while (true) {
                        while (buffer.isEmpty()) {
                            buffer.wait();
                        }
                        int i = buffer.remove(0);
                        System.out.println("Consuming: " + i);
                        buffer.notifyAll();
                        Thread.sleep(500); // 模拟消费时间
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}