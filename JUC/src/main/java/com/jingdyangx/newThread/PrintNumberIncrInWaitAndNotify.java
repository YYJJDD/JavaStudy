package com.jingdyangx.newThread;

public class PrintNumberIncrInWaitAndNotify {

    private static int number;
    private static final Object object = new Object();

    public static void main(String[] args) throws InterruptedException{
        new Thread(new ToolClass(), "偶数").start();
        //这里休眠10ms保证偶数线程能够先执行
        Thread.sleep(10);
        new Thread(new ToolClass(), "奇数").start();
    }

    static class ToolClass implements Runnable {
        @Override
        public void run() {
            while (number <= 100) {
                synchronized (object) {
                    System.out.println(Thread.currentThread().getName() + "：" + number++);
                    object.notify();
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}