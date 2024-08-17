package com.jingdyangx.createThread;

class MyThread extends Thread {

    @Override
    public void run() {
        try {
            System.out.println("Start......");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Hello World: " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        MyThread thread2 = new MyThread();
        thread.start(); // 启动线程
        thread2.start();
    }
}