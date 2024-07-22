package com.jingdyang.aop.aspect;

import com.jingdyang.aop.aspect.config.SpringConfig;
import com.jingdyang.aop.aspect.dao.BookDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    /**
     * 执行顺序：
     * 1、未抛出异常时：环绕通知@Aroud -> 前置通知@Before -> 返回后通知@AfterReturning -> @After后置通知
     * 2、抛出异常时：环绕通知@Aroud -> 前置通知@Before -> 抛出异常后通知@AfterThrowing -> @After后置通知
     */
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        BookDao bookDao = ctx.getBean(BookDao.class);
        String name = bookDao.findName(100,"itheima");
        System.out.println(name);
    }
}
