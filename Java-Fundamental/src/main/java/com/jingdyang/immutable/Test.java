package com.jingdyang.immutable;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        ImmutableList immutableList = new ImmutableList();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        List<Integer> integers = immutableList.newList(list);
        integers.add(1);
        System.out.println(integers);
    }
}
