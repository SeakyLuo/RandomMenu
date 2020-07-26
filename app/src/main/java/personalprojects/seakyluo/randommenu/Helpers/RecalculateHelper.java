package personalprojects.seakyluo.randommenu.Helpers;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class RecalculateHelper {
    public static void RecalculateTags(){
        Map<String, Integer> map = new HashMap<>();
        Settings.settings.Foods.ForEach(f -> {
            f.GetTags().ForEach(t -> {
                int count = map.getOrDefault( t.Name,0);
                map.put(t.Name, ++count);
            });
        });
        Settings.settings.Tags = new AList<>(map.entrySet().stream()
                                                .map(e -> new Tag(e.getKey(), e.getValue()))
                                                .sorted().collect(Collectors.toList())).Reverse();
    }

    public static void FindAbnormalFood(){
        Settings.settings.Foods.ForEach(f -> {
            if (f.Images.Count() > 10){
                Log.d("fuck", f.Name);
            }
        });
    }
}
