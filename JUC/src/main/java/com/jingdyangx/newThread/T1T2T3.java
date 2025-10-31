package com.jingdyangx.newThread;

public class T1T2T3 {
    public static void main(String[] args) {
        // 创建线程对象
        Thread t1 = new Thread(() -> {
            System.out.println("t1");
        });
        Thread t2 = new Thread(() -> {
            try {
                t1.join();  // 加入线程t1，阻塞等待，只有t1线程执行完毕，再开始继续执行该线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2");
        });
        Thread t3 = new Thread(() -> {
            try {
                t2.join();  // 加入线程t1，阻塞等待，只有t1线程执行完毕，再开始继续执行该线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t3");
        });

        // 启动线程
        t1.start();
        t2.start();
        t3.start();
    }
}
