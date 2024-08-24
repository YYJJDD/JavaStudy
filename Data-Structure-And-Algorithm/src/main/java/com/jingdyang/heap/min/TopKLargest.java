package com.jingdyang.heap.min;

import java.util.*;

/**
 * 小顶堆（最小堆）
 * 347. 前 K 个高频元素 https://leetcode.cn/problems/top-k-frequent-elements/description/
 */
public class TopKLargest {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 6, 6, 1, 8, 6, 0, 7, 9, 2};
        int k = 2;
        int[] kthLargest = topKFrequent(nums, k);
        System.out.println("数组：" + Arrays.toString(nums) + "中，前" + k + "个高频数字为：" + Arrays.toString(kthLargest));
    }

    // 最小堆
    public static  int[] topKFrequent(int[] nums, int k) {
        // 使用字典，统计每个元素出现的次数，元素为键，元素出现的次数为值
        HashMap<Integer,Integer> map = new HashMap();
        for(int num : nums){
            if (map.containsKey(num)) {
                map.put(num, map.get(num) + 1);
            } else {
                map.put(num, 1);
            }
        }
        // 遍历map，用最小堆保存频率最大的k个元素
        PriorityQueue<Integer> pq = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return map.get(a) - map.get(b);
            }
        });
        for (Integer key : map.keySet()) {
            // 注释部分逻辑可以进行如下优化，直接向优先队列中加入元素，如果超过k个，直接让队首元素出队
            // if (pq.size() < k) {
            //     pq.add(key);
            // } else if (map.get(key) > map.get(pq.peek())) {
            //     pq.remove();
            //     pq.add(key);
            // }
            pq.add(key);
            if(pq.size() > k) {
                pq.remove();
            }
        }
        // 取出最小堆中的元素
        List<Integer> res = new ArrayList<>();
        while (!pq.isEmpty()) {
            res.add(pq.remove());
        }
        return res.stream().mapToInt(Integer::intValue).toArray();
    }
}
