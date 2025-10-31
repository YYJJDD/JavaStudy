package com.jingdyang.sort;

public class SelectBubbleInsert {

    // 数组中交换i和j位置的数
    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // 选择排序
    public static void selectionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int minIndex, i = 0; i < arr.length - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            swap(arr, i, minIndex);
        }
    }

    // 冒泡排序
    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        // 每轮外层for loop结束后，[当前索引end, arr.length - 1]变为有序且最大，每轮将当前[0, end]位置的最大值放到end位置
        for (int end = arr.length - 1; end > 0; end--) {
            for (int i = 0; i < end; i++) {
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                }
            }
        }
    }

    /**
     * 插入排序：
     * 受原数组顺序情况影响：
     * 最好情况：[1,2,3,4,5,6], 不会走到内层for loop, 需要交换0次；
     * 最坏情况：[6,5,4,3,2,1], 每轮内层for loop都会进入且每次都会发生交换
     */
    public static void insertionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        // 外层循环，保证每轮循环结束索引[0, i]位置有序
        for (int i = 1; i < arr.length; i++) {
            // 将第i个元素放到[0, i]的某个位置上，使得索引[0, i]位置有序（该循环开始时[0, i - 1]位置已经有序）
            for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                swap(arr, j, j + 1);
            }
        }
    }

}