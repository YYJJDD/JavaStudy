import java.util.ArrayList;
import java.util.List;

class Solution {

    List<Integer> ans = new ArrayList<>();

    public static void main(String[] args) {
        Solution solution = new Solution();
        int[][] matrix = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        for(int i = 0; i < matrix.length; i++){
            for (int j = 0;j < matrix[0].length; j++) {
                System.out.println(matrix[i][j]);
            }
        }
        solution.spiralOrder(matrix);
//        System.out.println(ans);
    }




    public List<Integer> spiralOrder(int[][] matrix) {
        int left = 0, right = matrix[0].length - 1;
        int top = 0, bottom = matrix.length - 1;
        for (; left <= right && top <= bottom; ) {
            left2Right(matrix, top, left, right);
            top++;
            top2Bottom(matrix, right, top, bottom);
            right--;
            right2Left(matrix, bottom, left, right);
            bottom--;
            bottom2Top(matrix, left, top, bottom);
            left++;
        }
        return ans;
    }

    private void left2Right(int[][] matrix, int top, int left, int right) {
        for (int i = left; i <= right; i++) {
            ans.add(matrix[top][i]);
        }
    }

    private void top2Bottom(int[][] matrix, int right, int top, int bottom) {
        for (int i = top; i <= bottom; i++) {
            ans.add(matrix[i][right]);
        }
    }

    private void right2Left(int[][] matrix, int bottom, int left, int right) {
        for (int i = right; i >= left; i--) {
            ans.add(matrix[bottom][i]);
        }
    }

    private void bottom2Top(int[][] matrix, int left, int top, int bottom) {
        for (int i = bottom; i >= top; i--) {
            ans.add(matrix[i][left]);
        }
    }
}