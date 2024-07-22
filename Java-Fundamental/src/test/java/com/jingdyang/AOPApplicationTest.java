package com.jingdyang;

import com.jingdyang.test.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.annotation.Annotation;

@Slf4j
public class AOPApplicationTest {

    @Test
    public void classTest() throws Exception {
        // 获取Class对象的三种方式
        log.info("根据类名:  \t" + User.class);
        log.info("根据对象:  \t" + new User().getClass());
        log.info("根据全限定类名:\t" + Class.forName("com.jingdyang.test.User"));
        Class<User> userClass = User.class;
        // 常用的方法
        log.info("获取全限定类名:\t" + userClass.getName());
        log.info("获取类名:\t" + userClass.getSimpleName());
        log.info("实例化:\t" + userClass.newInstance());

        Annotation[] annotations = userClass.getAnnotations();
        return;

    }

}
