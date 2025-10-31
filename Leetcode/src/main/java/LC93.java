import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LC93 {

    public static void main(String[] args) {
        String s = "25525511135";
        List<String> list = new LC93().restoreIpAddresses(s);
        System.out.println(list);
    }


    List<List<String>> ans = new ArrayList<>();
    LinkedList<String> track = new LinkedList<>();

    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        backtrack(s, 0);
        for (int i = 0; i < ans.size(); i++) {
            List<String> cur = ans.get(i);
            // String str = Strings.join(cur, ".");
            String str = "";
            for (int j = 0; j < cur.size() - 1; j++) {
                str += cur.get(j) + ".";
            }
            str += cur.get(cur.size() - 1);
            result.add(str);
        }
        return result;
    }

    private void backtrack(String s, int startIndex) {
        if (startIndex >= s.length()) {
            ans.add(new ArrayList<>(track));
            return;
        }
        for (int i = startIndex; i < s.length(); i++) {
            for (int j = 1; j <= 3 && i + j < s.length(); j++) {
                String temp = s.substring(i, i + j);
                if (check(temp)) {
                    track.add(temp);
                    backtrack(s, i + j);
                    track.removeLast();
                }
            }
        }
    }

    private boolean check(String s) {
        if (s.length() > 0 && Integer.parseInt(s) >= 0 && Integer.parseInt(s) <= 255) {
            return true;
        }
        return false;
    }
}
