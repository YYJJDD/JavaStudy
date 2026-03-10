package com.jingdyangx;

public class TestQuiz1 {
    
    public static class PrintThread extends Thread {
        PrintThread(String name) {
            super(name);
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.print(getName() + ": " + i);
                System.out.printf(" ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        
        PrintThread t1 = new PrintThread("A");
        PrintThread t2 = new PrintThread("B");
        PrintThread t3 = new PrintThread("C");

        try {
//            t1.start();
//            t1.join();
//
//            t2.start();
//            t2.join();
//
//            t3.start();
//            t3.join();
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