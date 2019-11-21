package personalprojects.seakyluo.randommenu;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Settings {
    public static String FILENAME = "RandomMenuSettings.json";
    public static Settings settings;
    public ArrayList<Food> Foods = new ArrayList<>();
    public ArrayList<Tag> Tags = new ArrayList<>();
    public ArrayList<String> ToCook = new ArrayList<>();
    public Food FoodDraft;

    public void AddFood(Food food){
        Foods.add(food);
        HashSet<Tag> tags = new HashSet<>(this.Tags);
        for (Tag tag: food.GetTags()){
            tag.More();
            if (!tags.contains(tag)){
                this.Tags.add(tag);
            }
        }
        SortTags();
    }

    public void RemoveFood(Food food){
        Foods.remove(food);
        for (Tag tag: new ArrayList<>(Tags)){
            if (food.HasTag(tag) && tag.Less() == 0)
                Tags.remove(tag);
        }
        SortTags();
    }

    public void SortTags(){
        Collections.sort(Tags, Tag::compareTo);
        Collections.reverse(Tags);
    }

    public void UpdateFood(Food before, Food after){
        RemoveFood(before);
        AddFood(after);
    }

    public boolean ContainsFood(String food_name){
        for (Food food: Foods)
            if (food.Name.equals(food_name))
                return true;
        return false;
    }

    public boolean ContainsTag(String tag_name){
        for (Tag tag: Tags)
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
