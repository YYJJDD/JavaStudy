package com.jingdyangx;

import com.jingdyangx.async.ThreadPoolConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JUCApplication {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        SpringApplication.run(JUCApplication.class, args);
    }
}
