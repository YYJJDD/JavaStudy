package com.jingdyang.aop.aspect.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.jingdyang.aop.aspect")
@EnableAspectJAutoProxy
public class SpringConfig {
}
