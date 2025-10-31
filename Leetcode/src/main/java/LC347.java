import java.util.*;

public class LC347 {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> mp = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int times = mp.getOrDefault(nums[i], 0);
            mp.put(nums[i], times + 1);
        }
        List<Integer> list = mp.values().stream().sorted(Comparator.reverseOrder()).toList();
        list = list.subList(0, k);
        Set<Integer> set = new HashSet<>(list);
        List<Integer> ans = new ArrayList<>();
        mp.forEach((key, value) -> {
            if (set.contains(value)) {
                ans.add(key);
            }
        });
        return ans.stream().mapToInt(Integer::intValue).toArray();
    }
}