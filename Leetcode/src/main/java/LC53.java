/**
 * 53. 最大子数组和
 * https://leetcode.cn/problems/maximum-subarray/?envType=study-plan-v2&envId=top-100-liked
 */
class LC53 {
    public int maxSubArray(int[] nums) {
        int len = nums.length;
        // 动态规划：dp[i]表示以i结尾的子数组和最大值
        // 状态转移方程：dp[i] = Math.max(nums[i], dp[i -  1] + nums[i])
        int[] dp = new int[len];
        dp[0] = nums[0];
        int res = nums[0];
        for (int i = 1; i < len; i++) {
            dp[i] = Math.max(dp[i - 1] + nums[i], nums[i]);
            res = Math.max(res, dp[i]);
        }
        return res;
    }
}