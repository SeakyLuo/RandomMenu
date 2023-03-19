package personalprojects.seakyluo.randommenu.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.MatchFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SearchUtils {
    public static final int MAX_POINTS = 100, SECOND_MAX_POINTS = 95, THIRD_MAX_POINTS = 90, FOURTH_MAX_POINTS = 85;

    public static int evalString(String text, String keyword){
        if (StringUtils.isEmpty(text)) return 0;
        if (text.equals(keyword)) return MAX_POINTS;
        if (text.startsWith(keyword)) return SECOND_MAX_POINTS;
        if (text.endsWith(keyword)) return THIRD_MAX_POINTS;
        if (text.contains(keyword)) return FOURTH_MAX_POINTS;
        int editDistance = getEditDistance(text, keyword);
        int ratio = editDistance * 100 / Math.max(text.length(), keyword.length());
        if (ratio <= 60) return FOURTH_MAX_POINTS - ratio;
        return 0;
    }

    public static MatchFood evalFood(SelfMadeFood food, String keyword){
        int namePoints = evalString(food.getName(), keyword), tagPoints = evalFoodTag(food, keyword), notePoints = evalFoodNote(food, keyword);
        int points = namePoints + tagPoints + notePoints;
        int bonus = 0;
        if (points > 0){
            bonus -= food.getHideCount();
            if (food.isFavorite()) bonus += 10;
        }
        return new MatchFood(food, points, bonus, namePoints, tagPoints, notePoints);
    }

    public static int evalFoodTag(SelfMadeFood food, String keyword){
        int points = 0;
        for (Tag t: food.getTags()) {
            points = Math.max(points, evalString(t.getName(), keyword) - 15);
            if (points == MAX_POINTS - 15){
                break;
            }
        }
        return points;
    }

    public static int evalFoodNote(SelfMadeFood food, String keyword){
        String note = food.getNote();
        if (StringUtils.isBlank(note)) return 0;
        int points = evalString(note, keyword);
        return points == MAX_POINTS ? 120 : Math.max(points - 30, 0);
    }

    public static List<String> searchTags(List<Tag> tags, String keyword){
        if (StringUtils.isEmpty(keyword)) return new ArrayList<>();
        return tags.stream()
                .filter(t -> t.getName().contains(keyword))
                .sorted((t1, t2) -> {
                    int res = SearchUtils.evalString(t1.getName(), keyword) - SearchUtils.evalString(t2.getName(), keyword);
                    return res == 0 ? t2.getCounter() - t1.getCounter() : res;
                })
                .map(Tag::getName)
                .collect(Collectors.toList());
    }


    private static int getEditDistance(String target, String given) {
        int n = target.length();
        int m = given.length();

        // 有一个字符串为空串
        if (n * m == 0) {
            return n + m;
        }
        // init
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            int[] list = new int[m + 1];
            list[0] = i;
            if (i == 0)  {
                for (int j = 1; j < m + 1; j++) list[j] = j;
            } else {
                for (int j = 1; j < m + 1; j++) list[j] = 0;
            }
            dp[i] = list;
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = dp[i - 1][j] + 1;
                int down = dp[i][j - 1] + 1;
                int left_down = dp[i - 1][j - 1];
                if (target.charAt(i - 1) != given.charAt(j - 1)) {
                    left_down += 1;
                }
                dp[i][j] = Math.min(Math.min(left, down), left_down);
            }
        }
        return dp[n][m];
    }
}
