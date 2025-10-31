package com.jingdyangx.ProducerConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerBlockingQueue {

    private int count = 0;

    private BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) {

        ProducerConsumerBlockingQueue demo2 = new ProducerConsumerBlockingQueue();
        for (int i = 1; i <= 5; i++) {
            new Thread(demo2.new Producer(), "生产者-" + i).start();
            new Thread(demo2.new Consumer(), "消费者-" + i).start();
        }
    }

    class Producer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.put(1);
                    System.out.println("生产者 " + Thread.currentThread().getName() + " 总共有 " + ++count + " 个资源");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.take();
                    System.out.println("消费者 " + Thread.currentThread().getName() + " 总共有 " + --count + " 个资源");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}