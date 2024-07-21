package com.jingdyang.reflect.utils;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 反射工具类
 */
@Slf4j
public class ReflectUtils {
    public static void invokeSetMethod(Object obj,
                                       String fieldName,
                                       Object fieldValue) {
        try {
            String firstWorld = fieldName.substring(0, 1).toUpperCase();
            String methodName = String.format("set%s%s", firstWorld, fieldName.substring(1));
            Method method = obj.getClass().getMethod(methodName, fieldValue.getClass());
            method.invoke(obj, fieldValue);
        } catch (Exception e) {
            log.error("invokeSetMethod error:", e);
        }
    }

    public static Object invokeGetMethod(Object obj,
                                         String fieldName) {
        Method method = ReflectUtil.getMethod(obj.getClass(),
                true, "get" + fieldName);
        try {
            return method.invoke(obj);
        } catch (Exception e) {
            log.error("invokeSetMethod error:", e);
        }
        return null;
    }

    public static String invokeGetStringMethod(Object obj,
                                               String fieldName) {
        Object ret = invokeGetMethod(obj, fieldName);
        if (Objects.nonNull(ret)) {
            return ret.toString();
        }

        return "";
    }
}
