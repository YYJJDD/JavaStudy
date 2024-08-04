package com.jingdyang.spel;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
     * bean引用
     * 如果评估上下文已经配置了 bean 解析器，可以使用 @ 符号从表达式中查找 bean。直接看案例。
     */
    @Configuration
    @ComponentScan
    public class BeanReferencesTest {
        // 注入一个bean
        @Component("myService")
        static class MyService {
            public String getString(String name){
                return name + ": Hello, World!";
            }
        }

        public static void main(String[] args) {
            AnnotationConfigApplicationContext applicationContext =
                    new AnnotationConfigApplicationContext(BeanReferencesTest.class);
            SpelExpressionParser parser = new SpelExpressionParser();
            // 使用 StandardEvaluationContext
            StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
            // 需要注入一个BeanResolver来解析bean引用，此处注入 BeanFactoryResolver
            standardEvaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
            // 使用 @ 来引用bean
            MyService myService = parser.parseExpression("@myService").getValue(standardEvaluationContext, MyService.class);
            System.out.println(myService);

            String value = parser.parseExpression("@myService.getString('lbw')").getValue(standardEvaluationContext, String.class);
            System.out.println(value);
        }
    }