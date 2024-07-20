package com.jingdyang.proxy.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodProxy;

public interface MethodInterceptor
        extends Callback {
    // 拦截被代理类中的方法
    public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, MethodProxy proxy) throws Throwable;
}