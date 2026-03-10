package com.jingdyangx.newThread;

public class ConcurrentAddition {

    private static final Object lock = new Object();
    private static int sum = 0;
    private static boolean evenTurn = true;

    public static void main(String[] args) {
        Thread threadEven = new Thread(new AdditionTask(lock, "Even"));
        Thread threadOdd = new Thread(new AdditionTask(lock, "Odd"));

        threadEven.start();
        threadOdd.start();
    }

    static class AdditionTask implements Runnable {

        private final Object lock;
        private final String name;

        public AdditionTask(Object lock, String name) {
            this.lock = lock;
            this.name = name;
        }

        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 1; i <= 100; i++) {
                    while (!evenTurn && name.equals("Even") || evenTurn && name.equals("Odd")) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (name.equals("Even") && i % 2 == 0) {
                        sum += i;
                        System.out.println(name + ": Adding " + i + " Sum: " + sum);
                    } else if (name.equals("Odd") && i % 2 != 0) {
                        sum += i;
                        System.out.println(name + ": Adding " + i + " Sum: " + sum);
                    }
                    evenTurn = !evenTurn;
                    lock.notify();
                }
            }
        }
    }
}