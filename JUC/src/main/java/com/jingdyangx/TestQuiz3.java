package com.jingdyangx;

public class TestQuiz3 {
    private static final Object lock = new Object();
    private static int number = 1;
    private static char letter = 'A';
    private static boolean printNumber = true;

    public static void main(String[] args) {
        Thread numberThread = new Thread(new NumberPrinter());
        Thread letterThread = new Thread(new LetterPrinter());

        numberThread.start();
        letterThread.start();
    }

    static class NumberPrinter implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                while (number <= 52) {
                    if (printNumber) {
                        System.out.print(number++);
                        System.out.print(number++);
                        printNumber = false;
                        lock.notify();
                    } else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
    }

    static class LetterPrinter implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                while (letter <= 'Z') {
                    if (!printNumber) {
                        System.out.print(letter++);
                        printNumber = true;
                        lock.notify();
                    } else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
    }
}