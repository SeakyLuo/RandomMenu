package personalprojects.seakyluo.randommenu.models;

import androidx.annotation.NonNull;

import personalprojects.seakyluo.randommenu.utils.JsonUtils;

public class Settings {
    public static String FILENAME = "RandomMenuSettings.json";
    public static Settings settings;

    public AList<String> ToCook = new AList<>();
    public AList<String> ToEat = new AList<>();
    public AList<String> DislikeFood = new AList<>();
    public AList<String> SearchHistory = new AList<>();
    public SelfFood FoodDraft;
    public String Note = "";
    public boolean AutoTag = true;

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
