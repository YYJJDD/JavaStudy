package com.jingdyang.chainOfResponsibility.demo2;

// 人事处处理
public class HRHandler extends DayOffHandler {
    @Override
    public void handle(String request) {
        System.out.println("人事处审查: " + request);
        System.out.println("同意请求，记录请假");
        if (getNext() != null) {
            getNext().handle(request);
        }
    }
}