//package com.jingdyang.sort;
//
//
//import com.jingdyang.utils.SortUtil;
//
//public class BubbleSort {
//
//    // 冒泡排序
//    public static void bubbleSort(int[] arr) {
//        if (arr == null || arr.length < 2) {
//            return;
//        }
//        // 每轮外层for loop结束后，[当前索引end, arr.length - 1]变为有序且最大，每轮将当前[0, end]位置的最大值放到end位置
//        for (int end = arr.length - 1; end > 0; end--) {
//            for (int i = 0; i < end; i++) {
//                if (arr[i] > arr[i + 1]) {
//                    SortUtil.swap(arr, i, i + 1);
//                }
//            }
//        }
//    }
//
//}
