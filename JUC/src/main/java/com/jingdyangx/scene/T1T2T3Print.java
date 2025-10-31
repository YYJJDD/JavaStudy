package com.jingdyangx.scene;

public class T1T2T3Print {

    public static class PrintThread extends Thread {
        PrintThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.print(getName());
            System.out.println();
        }
    }

    public static void main(String[] args) {

        PrintThread t1 = new PrintThread("A");
        PrintThread t2 = new PrintThread("B");
        PrintThread t3 = new PrintThread("C");

        try {
            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}