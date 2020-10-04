package personalprojects.seakyluo.randommenu.models;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {
    public static String FILENAME = "RandomMenuSettings.json";
    public static Settings settings;
    public AList<Food> Foods = new AList<>();
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

    public void putTagMapper(TagMapper tagMapper){
        AutoTagMap.put(tagMapper.key, tagMapper.value.stream().map(t -> t.Name).collect(Collectors.toList()));
    }
    public void addFood(Food food, int index){
        food.getTags().forEach(tag -> {
            int tag_index = Tags.indexOf(tag);
            if (tag_index == -1){
                Tags.add(tag.more());
            }else{
                tag.Counter = Tags.get(tag_index).more().Counter;
            }
        });
        Foods.add(food, index);
        sortTags();
        if (food.isFavorite()){
            MyFavorites.add(food.Name, 0);
        }
    }
    public void addFood(Food food){ addFood(food, 0); }

    public void removeFood(Food food){
        Foods.remove(food);
        Tags.removeAll(Tags.find(food::hasTag).forEach(Tag::less).find(Tag::isEmpty));
        sortTags();
        MyFavorites.remove(food.Name);
    }

    public void sortTags(){ Tags.sort(Tag::compareTo).reverse(); }

    public void updateFood(Food before, Food after){
        int index = Foods.indexOf(before);
        if (index == -1) return;
        Foods.set(after, index);
        AList<Tag> a = after.getTags(), b = before.getTags(),
                   add = a.setDifference(b), remove = b.setDifference(a);
        Tags.forEach(t -> {
            if (remove.contains(t))
                t.less();
        }).remove(Tag::isEmpty);
        Tags.forEach(t -> {
            if (add.contains(t))
                add.remove(t.more());
        });
        Tags.addAll(add.forEach(Tag::more));
        sortTags();
        if (before.isFavorite() && !after.isFavorite()){
            MyFavorites.remove(before.Name);
        }
        else if (!before.isFavorite() && after.isFavorite()){
            MyFavorites.add(after.Name, 0);
        }
    }

    public void setFavorite(Food food, boolean favorite){
        Food target = Foods.first(food);
        target.setIsFavorite(favorite);
        if (favorite) MyFavorites.add(target.Name, 0);
        else MyFavorites.remove(target.Name);
    }

    public AList<Food> getFavoriteFoods(){
        AList<Food> list = new AList<>();
        MyFavorites.forEach(name -> list.add(Foods.first(f -> f.Name.equals(name))));
        return list;
    }

    public static Settings fromJson(String json){
        Gson gson = new Gson();
        try{
            return gson.fromJson(json, Settings.class);
        }catch (Exception e){
            return new Settings();
        }
    }

    @NonNull
    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (!(obj instanceof Settings)) return false;
        return toString().equals(((Settings)obj).toString());
    }
}
