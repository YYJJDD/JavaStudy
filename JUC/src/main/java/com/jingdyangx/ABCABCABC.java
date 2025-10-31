package com.jingdyangx;

public class ABCABCABC {
    private int flag = 0;
    public synchronized void printA() {
        for(int i = 0; i < 10; i++) {
            while (flag != 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            flag = 1;
            System.out.print("A");
            notifyAll();
        }
    }

    public synchronized void printB() {
        for(int i = 0; i < 10; i++) {
            while (flag != 1) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            flag = 2;
            System.out.print("B");
            notifyAll();
        }
    }

    public synchronized void printC() {
        for(int i = 0; i < 10; i++) {
            while (flag != 2) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            flag = 0;
            System.out.print("C");
            notifyAll();
        }
    }

    public static void main(String[] args) {
        ABCABCABC abcabcabc = new ABCABCABC();

        new Thread(() -> abcabcabc.printA()).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                abcabcabc.printB();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                abcabcabc.printC();
            }
        }).start();
    }
}