package com.jingdyangx;

public class TestQuiz2 {
    private static final int MAX_NUMBER = 100;
    private static final Object lock = new Object();
    private static int currentNumber = 1;

    public static void main(String[] args) {
        Thread thread1 = new Thread(new NumberPrinter(1));
        Thread thread2 = new Thread(new NumberPrinter(2));

        thread1.start();
        thread2.start();
    }

    static class NumberPrinter implements Runnable {
        private final int threadNumber;

        public NumberPrinter(int threadNumber) {
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            while (currentNumber <= MAX_NUMBER) {
                synchronized (lock) {
                    if (currentNumber % 2 != threadNumber - 1) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        System.out.println("Thread " + threadNumber + ": " + currentNumber);
                        currentNumber++;
                        lock.notifyAll();
                    }
                }
            }
        }
    }
}