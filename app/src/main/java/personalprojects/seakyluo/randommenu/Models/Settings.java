package personalprojects.seakyluo.randommenu.Models;

import com.google.gson.Gson;

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

    public void AddFood(Food food, int index){
        food.GetTags().ForEach(tag -> {
            tag.More();
            int tag_index = Tags.IndexOf(tag);
            if (tag_index > -1) Tags.Set(tag, tag_index);
            else Tags.Add(tag);
        });
        Foods.Add(food, index);
        SortTags();
    }
    public void AddFood(Food food){ AddFood(food, 0); }

    public void RemoveFood(Food food){
        Foods.Remove(food);
        Tags.RemoveAll(Tags.Find(food::HasTag).ForEach(Tag::Less).Find(Tag::IsEmpty));
        SortTags();
    }

    public void SortTags(){ Tags.Sort(Tag::compareTo).Reverse(); }

    public void UpdateFood(Food before, Food after){
        int index = Foods.IndexOf(before);
        if (index == -1) return;
        Foods.Set(after, index);
        AList<Tag> a = after.GetTags(), b = before.GetTags(),
                   add = a.SetDifference(b), remove = b.SetDifference(a);
        Tags.ForEach(t -> {
            if (remove.Contains(t))
                t.Less();
        }).Remove(Tag::IsEmpty);
        Tags.ForEach(t -> {
            if (add.Contains(t))
                add.Remove(t.More());
        });
        Tags.AddAll(add.ForEach(Tag::More));
        SortTags();
    }

    public void SetFavorite(Food food, boolean favorite){
        Food target = Foods.First(food);
        target.SetIsFavorite(favorite);
        if (favorite) MyFavorites.Add(target.Name, 0);
        else MyFavorites.Remove(target.Name);
    }

    public AList<Food> GetFavoriteFoods(){
        AList<Food> list = new AList<>();
        MyFavorites.ForEach(name -> list.Add(Foods.First(f -> f.Name.equals(name))));
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
}
