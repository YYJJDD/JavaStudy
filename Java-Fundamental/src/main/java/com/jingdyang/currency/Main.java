package com.jingdyang.currency;

public class Main {
    //    private static int a = 0;
    private static volatile int a = 0;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0) ;
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        a = 1;   //很明显，按照我们的逻辑来说，a的值被修改那么另一个线程将不再循环
    }
}