package com.jingdyang.proxy.staticProxy;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsProxy smsProxy = new SmsProxy(smsService);
        smsProxy.send("java");

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        List<Integer> unmodifiable = Collections.unmodifiableList(list);
        unmodifiable.add(666);
    }
}
