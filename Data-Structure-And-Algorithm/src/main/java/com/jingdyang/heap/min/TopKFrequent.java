package com.jingdyang.heap.min;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 小顶堆（最小堆）
 * 347. 前 K 个高频元素 https://leetcode.cn/problems/top-k-frequent-elements/description/
 */
public class TopKFrequent {

    public static void main(String[] args) {
        int[] nums = new int[]{4, 5, 3, 1, 8, 6, 0, 7, 9, 2};
        int k = 3;
        int kthLargest = findKthLargest(nums, k);
        System.out.println("数组：" + Arrays.toString(nums) + "中，第" + k + "大的数字为：" + kthLargest);
        System.out.println("数组：" + Arrays.toString(nums) + "中，第" + k + "大的数字为：" + findKthLargest2(nums, k));
    }

    /**
     * lc215. 数组中的第K个最大元素 https://leetcode.cn/problems/kth-largest-element-in-an-array/
     */
    public static int findKthLargest(int[] nums, int k) {
        int len = nums.length;
        // 使用一个含有 k 个元素的最小堆，PriorityQueue 底层是动态数组，为了防止数组扩容产生消耗，可以先指定数组的长度
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k, Comparator.comparingInt(a -> a));
        // Java 里没有 heapify ，因此我们逐个将前 k 个元素添加到 minHeap 里
        for (int i = 0; i < k; i++) {
            minHeap.offer(nums[i]);
        }

        for (int i = k; i < len; i++) {
            // 看一眼，不拿出，因为有可能没有必要替换
            Integer topElement = minHeap.peek();
            // 只要当前遍历的元素比堆顶元素大，堆顶弹出，遍历的元素进去
            if (nums[i] > topElement) {
                // Java 没有 replace()，所以得先 poll() 出来，然后再放回去
                minHeap.poll();
                minHeap.offer(nums[i]);
            }
        }
        return minHeap.peek();
    }

    /**
     * 相比方法findKthLargest，直接向优先队列中加入元素，如果超过k个，直接让队首元素出队
     * @param nums
     * @param k
     * @return
     */
    public static int findKthLargest2(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int num : nums) {
            heap.add(num);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        return heap.peek();
    }

}
