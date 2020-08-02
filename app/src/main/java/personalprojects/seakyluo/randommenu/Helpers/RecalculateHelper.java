package personalprojects.seakyluo.randommenu.helpers;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class RecalculateHelper {
    public static void RecalculateTags(){
        Map<String, Integer> map = new HashMap<>();
        Settings.settings.Foods.forEach(f -> {
            f.GetTags().forEach(t -> {
                int count = map.getOrDefault( t.Name,0);
                map.put(t.Name, ++count);
            });
        });
        Settings.settings.Tags = new AList<>(map.entrySet().stream()
                                                .map(e -> new Tag(e.getKey(), e.getValue()))
                                                .sorted().collect(Collectors.toList())).reverse();
    }

    public static void FindAbnormalFood(){
        Settings.settings.Foods.forEach(f -> {
            if (f.Images.count() > 10){
                Log.d("fuck", f.Name);
            }
        });
    }
}
