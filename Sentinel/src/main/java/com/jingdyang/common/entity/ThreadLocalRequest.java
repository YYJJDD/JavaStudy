package com.jingdyang.common.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ThreadLocalRequest {
    // 存储Key值限定
    public static final String APP_ID = "APP_ID";

    // 具体功能实现
    private static ThreadLocal<Map<String, Object>> mapThreadLocal = new ThreadLocal<>();

    public static Map<String, Object> getMap() {
        Map<String, Object> map = mapThreadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            mapThreadLocal.set(map);
        }
        return map;
    }

    public static void put(String key,
                           Object value) {
        getMap().put(key, value);
    }

    public static Object get(String key) {
        return getMap().get(key);
    }

    public static String getString(String key) {
        Object res = get(key);
        try {
            if (res != null) {
                return (String) res;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.debug("类型转换错误：key:" + key);
            return null;
        }
    }

    public static String getString(String key,
                                   String defaultValue) {
        String res = getString(key);
        if (res == null || res.isEmpty()) {
            return defaultValue;
        }
        return res;
    }

    public static Float getFloat(String key) {
        Object res = get(key);
        try {
            if (res != null) {
                return (Float) res;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.debug("类型转换错误：key:" + key);
            return null;
        }
    }

    public static Float getFloat(String key,
                                 Float defaultValue) {
        Float res = getFloat(key);
        if (res == null) {
            return defaultValue;
        }
        return res;
    }

    public static Long getLong(String key) {
        Object res = get(key);
        try {
            if (res != null) {
                return (Long) res;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.debug("类型转换错误：key:" + key);
            return null;
        }
    }

    public static Long getLong(String key,
                               Long defaultValue) {
        Long res = getLong(key);
        if (res == null) {
            return defaultValue;
        }
        return res;
    }

}
