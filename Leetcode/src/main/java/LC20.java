//        给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。
//
//        有效字符串需满足：
//
//        左括号必须用相同类型的右括号闭合。
//
//        左括号必须以正确的顺序闭合。
//
//        每个右括号都有一个对应的相同类型的左括号。
//
//        示例 1：
//
//        输入：s = "()"
//
//        输出：true
//
//        示例 2：
//
//        输入：s = "()[]{}"
//
//        输出：true
//
//        示例 3：
//
//        输入：s = "(]"
//
//        输出：false
//
//        示例 4：
//
//        输入：s = "([])"
//
//        输出：true
//
//        提示：
//
//        1 <= s.length <= 104
//
//        s 仅由括号 '()[]{}' 组成

//

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class LC20 {
    public boolean checkBracket(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int len = str.length();
        Map<Character, Character> mp = new HashMap<>();
        mp.put('(', ')');
        mp.put('[', ']');
        mp.put('{', '}');
        Deque<Character> dq = new LinkedList<>();
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            // 右括号
            if (!mp.containsKey(ch)) {
                if (dq.isEmpty()) {
                    return false;
                }
                char top = dq.pop();
                if (mp.get(top) != ch) {
                    return false;
                }
            }
            // 左括号
            else {
                dq.push(ch);
            }
        }
        return dq.isEmpty();
    }
}