package com.jingdyangx;

public class TenThreadSum {

    public static class SumThread extends Thread{

        int forct = 0;  int sum = 0;

        SumThread(int forct){
            this.forct = forct;
        }

        @Override
        public void run() {
            for(int i = 1; i <= 10; i++){
                sum += i + forct * 10;
            }
            System.out.println(getName() + "  " + sum);
        }
    }

    public static void main(String[] args) {

        int result = 0;

        for(int i = 0; i < 10; i++){
            SumThread sumThread = new SumThread(i);
            sumThread.start();
            try {
                sumThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = result + sumThread.sum;
        }
        System.out.println("result   " + result);
    }
}