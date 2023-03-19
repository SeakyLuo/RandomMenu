package personalprojects.seakyluo.randommenu.models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.dao.SelfFoodDAO;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;

public class Settings {
    public static String FILENAME = "RandomMenuSettings.json";
    public static Settings settings;
    public AList<TempFood> Foods = new AList<>();
    public AList<Tag> Tags = new AList<>();
    public AList<String> MyFavorites = new AList<>();
    public AList<String> ToCook = new AList<>();
    public AList<String> ToEat = new AList<>();
    public AList<String> DislikeFood = new AList<>();
    public AList<String> SearchHistory = new AList<>();
    public Food FoodDraft;
    public String Note = "";
    public boolean AutoTag = true;
    public LinkedHashMap<String, List<String>> AutoTagMap = new LinkedHashMap<>();

    public static Settings fromJson(String json){
        try{
            return JsonUtils.fromJson(json, Settings.class);
        }catch (Exception e){
            e.printStackTrace();
            return new Settings();
        }
    }

    @NonNull
    @Override
    public String toString(){
        return JsonUtils.toJson(this);
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Settings)) return false;
        return toString().equals(obj.toString());
    }
}
