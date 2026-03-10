
public class LC4 {

    public static void main(String[] args) {
        int[] nums1 = new int[]{1, 2};
        int[] nums2 = new int[]{3, 4};
        double medianSortedArrays = findMedianSortedArrays(nums1, nums2);
        System.out.println(medianSortedArrays);

    }

    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int[] nums = new int[len1 + len2];
        mergeSort(nums1, nums2, nums);
        double ans = 0;
        int len = nums.length;
        if (len % 2 == 1) {
            ans = nums[len / 2];
        } else {
            ans = (nums[len / 2] + nums[len / 2 - 1]) / 2.0;
        }
        return ans;
    }

    // 归并排序
    public static void mergeSort(int[] nums1, int[] nums2, int[] nums) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int i = 0;
        int j = 0;
        int index = 0;
        while (i < len1 && j < len2) {
            if (nums1[i] < nums2[j]) {
                nums[index++] = nums1[i++];
            } else {
                nums[index++] = nums2[j++];
            }
        }
        while (i < len1) {
            nums[index++] = nums1[i++];
        }
        while (j < len2) {
            nums[index++] = nums2[j++];
        }
    }
}
