package personalprojects.seakyluo.randommenu;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

public class Settings {
    public static String FILENAME = "RandomMenuSettings.json";
    public static Settings settings;
    public ArrayList<Food> foods = new ArrayList<>();
    public ArrayList<Tag> tags = new ArrayList<>();
    public ArrayList<String> toCook = new ArrayList<>();

    public void AddFood(Food food){
        foods.add(food);
        HashSet<Tag> tags = new HashSet<>(this.tags);
        for (Tag tag: food.GetTags()){
            tag.More();
            if (!tags.contains(tag)){
                this.tags.add(tag);
            }
        }
    }

    public void RemoveFood(Food food){
        foods.remove(food);
        for (Tag tag: new ArrayList<>(tags)){
            if (food.HasTag(tag) && tag.Less() == 0)
                tags.remove(tag);
        }
    }

    public void UpdateFood(Food before, Food after){
        RemoveFood(before);
        AddFood(after);
    }

    public boolean ContainsFood(String food_name){
        for (Food food: foods)
            if (food.Name.equals(food_name))
                return true;
        return false;
    }

    public boolean ContainsTag(String tag_name){
        for (Tag tag: tags)
            if (tag.getName().equals(tag_name))
                return true;
        return false;
    }

    public static Settings FromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, Settings.class);
    }

    public String ToJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
