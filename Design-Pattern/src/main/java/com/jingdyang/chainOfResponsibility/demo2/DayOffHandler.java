package com.jingdyang.chainOfResponsibility.demo2;

// 请假抽象处理器
public abstract class DayOffHandler {
    private DayOffHandler next;

    public DayOffHandler getNext() {
        return next;
    }

    public void setNext(DayOffHandler next) {
        this.next = next;
    }

    public abstract void handle(String request);
}







