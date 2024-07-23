package com.jingdyang.chainOfResponsibility.demo2;

/**
 * 以日常请假为例。请假申请会先到你的直属 leader 处审批，审批通过后再到部门 leader 处审批，部门 leader 通过后，最后到人事处报备记录请假天数。
 * 如果在传统企业里面，我们需要手写一份请假表，然后跑到直属 leader 办公室，让直属 leader 签字，然后再到部门 leader 办公室签字，
 * 最后还要跑到人事处上交请假单，这样相当于发出了三次请求，才能走完整个请假流程。
 * 在现代各种 OA 系统管理下，整个请假流程就变的简单了，我们只需要发起一次请假请求，接下来你的请假请求便会自动的在审批人中间进行流转，
 * 这个时候我们的责任链模式便派上用场。
 * https://segmentfault.com/a/1190000044126604
 */
public class DayOffDemo {
    public static void main(String[] args) {
        DayOffHandler groupLeaderHandler = new GroupLeaderHandler();
        DayOffHandler departmentLeaderHandler = new DepartmentLeaderHandler();
        DayOffHandler hrHandler = new HRHandler();
        groupLeaderHandler.setNext(departmentLeaderHandler);
        departmentLeaderHandler.setNext(hrHandler);

        System.out.println("收到面试通知，需要请假");
        String request = "家中有事，请假半天，望批准";
        System.out.println("发起请求：");
        groupLeaderHandler.handle(request);
    }
}
