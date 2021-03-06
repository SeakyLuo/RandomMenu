package personalprojects.seakyluo.randommenu.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.MatchFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SearchHelper {
    public static final int MAX_POINTS = 100, SECOND_MAX_POINTS = 95, THIRD_MAX_POINTS = 90, FOURTH_MAX_POINTS = 85;

    public static int evalString(String text, String keyword){
        int points = 0;
        if (text.equals(keyword)) points = MAX_POINTS;
        else if (text.startsWith(keyword)) points = SECOND_MAX_POINTS;
        else if (text.endsWith(keyword)) points = THIRD_MAX_POINTS;
        else if (text.contains(keyword)) points = FOURTH_MAX_POINTS;
        return points;
    }

    public static MatchFood evalFood(Food food, String keyword){
        int namePoints = evalString(food.Name, keyword), tagPoints = evalFoodTag(food, keyword), notePoints = evalFoodNote(food, keyword);
        int points = namePoints + tagPoints + notePoints;
        int bonus = 0;
        if (points > 0){
            bonus -= food.HideCount;
            if (food.isFavorite()) bonus += 10;
        }
        return new MatchFood(food, points, bonus, namePoints, tagPoints, notePoints);
    }

    public static int evalFoodTag(Food food, String keyword){
        int points = 0;
        for (Tag t: food.getTags().getList()) {
            points = Math.max(points, evalString(t.Name, keyword) - 15);
            if (points == MAX_POINTS - 15){
                break;
            }
        }
        return points;
    }

    public static int evalFoodNote(Food food, String keyword){
        if (Helper.isBlank(food.Note)) return 0;
        int points = evalString(food.Note, keyword);
        return points == MAX_POINTS ? 120 : Math.max(points - 30, 0);
    }

    public static List<String> searchTags(Stream<Tag> tags, String keyword){
        if (Helper.isNullOrEmpty(keyword)) return new ArrayList<>();
        return tags.filter(t -> t.Name.contains(keyword))
                    .sorted((t1, t2) -> {
                        int res = SearchHelper.evalString(t1.Name, keyword) - SearchHelper.evalString(t2.Name, keyword);
                        return res == 0 ? t2.getCounter() - t1.getCounter() : res;
                    })
                    .map(t -> t.Name)
                    .collect(Collectors.toList());
    }
}
