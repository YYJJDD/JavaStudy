package com.jingdyangx;

public class SaleTicket {

    static Object lock = new Object();
    int ticketNum = 10;


    public synchronized void getTicket() {
        synchronized (this) {
            if (ticketNum <= 0) {
                return;
            }
            // 非原子性操作
            ticketNum--;
            System.out.println(Thread.currentThread().getName() + "抢到一张票,剩余:" + ticketNum);
        }
    }

    public static void main(String[] args) {
        SaleTicket ticketDemo = new SaleTicket();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                ticketDemo.getTicket();
            }).start();
        }
    }


}