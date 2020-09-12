package personalprojects.seakyluo.randommenu.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SearchHelper {
    public static int evalString(String text, String keyword){
        int points = 0;
        if (text.equals(keyword)) points = 100;
        else if (text.startsWith(keyword)) points = 95;
        else if (text.endsWith(keyword)) points = 90;
        else if (text.contains(keyword)) points = 85;
        return points;
    }

    public static int evalFood(Food food, String keyword){
        int points = evalString(food.Name, keyword);
        if (points == 0){
            for (Tag t: food.getTags().getList()) {
                points = Math.max(points, evalString(t.Name, keyword) - 15);
                if (points == 85){
                    break;
                }
            }
        }
        if (!Helper.IsBlank(food.Note)){
            int notePoints = evalString(food.Note, keyword);
            if (notePoints == 100) points = 115;
            else points = Math.max(notePoints - 30, points);
        }
        if (points > 0){
            points -= food.HideCount;
            if (food.IsFavorite()) points += 10;
        }
        return points;
    }

    public static List<String> searchTags(Stream<Tag> tags, String keyword){
        if (Helper.IsNullOrEmpty(keyword)) return new ArrayList<>();
        return tags.filter(t -> t.Name.contains(keyword))
                    .sorted((t1, t2) -> {
                        int res = SearchHelper.evalString(t1.Name, keyword) - SearchHelper.evalString(t2.Name, keyword);
                        return res == 0 ? t2.getCounter() - t1.getCounter() : res;
                    })
                    .map(t -> t.Name)
                    .collect(Collectors.toList());
    }
}
