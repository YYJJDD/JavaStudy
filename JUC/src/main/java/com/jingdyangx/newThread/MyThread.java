package com.jingdyangx.newThread;

class Test extends Thread{
    private int i;
    public static void main(String[] args) {
        for(int j=0;j<50;j++){
            //获取当前线程
            System.out.println(Thread.currentThread().getName()+" "+j);
            if(j==10){
                //启动第一个线程
                new Test().start();
                //启动第二个线程
                new Test().start();
            }
        }
    }

    public void run(){
        for(;i<100;i++){
            System.out.println(this.getName()+" "+i);
        }
    }
}