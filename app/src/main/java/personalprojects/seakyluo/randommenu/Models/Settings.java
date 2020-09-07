package personalprojects.seakyluo.randommenu.models;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public void AddFood(Food food, int index){
        food.GetTags().forEach(tag -> {
            int tag_index = Tags.indexOf(tag);
            if (tag_index == -1){
                Tags.add(tag.More());
            }else{
                tag.Counter = Tags.get(tag_index).More().Counter;
            }
        });
        Foods.add(food, index);
        SortTags();
        if (food.IsFavorite()){
            MyFavorites.add(food.Name, 0);
        }
    }
    public void AddFood(Food food){ AddFood(food, 0); }

    public void RemoveFood(Food food){
        Foods.remove(food);
        Tags.removeAll(Tags.find(food::HasTag).forEach(Tag::Less).find(Tag::IsEmpty));
        SortTags();
        MyFavorites.remove(food.Name);
    }

    public void SortTags(){ Tags.sort(Tag::compareTo).reverse(); }

    public void updateFood(Food before, Food after){
        int index = Foods.indexOf(before);
        if (index == -1) return;
        Foods.set(after, index);
        AList<Tag> a = after.GetTags(), b = before.GetTags(),
                   add = a.setDifference(b), remove = b.setDifference(a);
        Tags.forEach(t -> {
            if (remove.contains(t))
                t.Less();
        }).remove(Tag::IsEmpty);
        Tags.forEach(t -> {
            if (add.contains(t))
                add.remove(t.More());
        });
        Tags.addAll(add.forEach(Tag::More));
        SortTags();
        if (before.IsFavorite() && !after.IsFavorite()){
            MyFavorites.remove(before.Name);
        }
        else if (!before.IsFavorite() && after.IsFavorite()){
            MyFavorites.add(after.Name, 0);
        }
    }

    public void SetFavorite(Food food, boolean favorite){
        Food target = Foods.first(food);
        target.SetIsFavorite(favorite);
        if (favorite) MyFavorites.add(target.Name, 0);
        else MyFavorites.remove(target.Name);
    }

    public AList<Food> GetFavoriteFoods(){
        AList<Food> list = new AList<>();
        MyFavorites.forEach(name -> list.add(Foods.first(f -> f.Name.equals(name))));
        return list;
    }

    public static Settings FromJson(String json){
        Gson gson = new Gson();
        try{
            return gson.fromJson(json, Settings.class);
        }catch (Exception e){
            return new Settings();
        }
    }

    public String ToJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (!(obj instanceof Settings)) return false;
        return ToJson().equals(((Settings)obj).ToJson());
    }
}
