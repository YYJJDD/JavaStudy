package com.jingdyang.chainOfResponsibility.demo2;

// 直属 leader 处理
public class GroupLeaderHandler extends DayOffHandler {
    @Override
    public void handle(String request) {
        System.out.println("直属 leader 审查: " + request);
        System.out.println("同意请求");
        if (getNext() != null) {
            getNext().handle(request);
        }
    }
}