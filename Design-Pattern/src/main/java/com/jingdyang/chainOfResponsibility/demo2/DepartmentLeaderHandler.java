package com.jingdyang.chainOfResponsibility.demo2;

// 部门 leader 处理
public class DepartmentLeaderHandler extends DayOffHandler{
    @Override
    public void handle(String request) {
        System.out.println("部门 leader 审查: " + request);
        System.out.println("同意请求");
        if (getNext() != null) {
            getNext().handle(request);
        }
    }
}