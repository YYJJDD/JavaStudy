package com.jingdyangx.newThread;

public class AlternatingPrint {

    private static final Object lock = new Object();

    public static void main(String[] args) {
        Thread threadA = new Thread(new PrintTask(lock, "A"));
        Thread threadB = new Thread(new PrintTask(lock, "B"));

        threadA.start();
        threadB.start();
    }

    static class PrintTask implements Runnable {

        private final Object lock;
        private final String name;

        public PrintTask(Object lock, String name) {
            this.lock = lock;
            this.name = name;
        }

        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 1; i <= 100; i++) {
                    while ((i % 2 == 0 && name.equals("A")) || (i % 2 != 0 && name.equals("B"))) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(name + ": " + i);
                    lock.notify();
                }
            }
        }
    }
}