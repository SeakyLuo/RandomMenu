package personalprojects.seakyluo.randommenu.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodImageDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodTagDaoService;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfFoodService {

    private static List<Consumer<Food>> foodDeletedListeners = new ArrayList<>();

    public static void addFoodDeletedListener(Consumer<Food> listener){
        foodDeletedListeners.add(listener);
    }

    public static void addFood(Food food){
        Food existing = SelfFoodDaoService.selectByName(food.getName());
        if (existing != null){
            return;
        }
        SelfFoodDaoService.insert(food);
        FoodTagService.increment(food);
        long id = food.getId();
        SelfFoodImageDaoService.insert(id, food.getImages());
        SelfFoodTagDaoService.insert(id, food.getTags());
    }

    public static void removeFood(Food food){
        SelfFoodDaoService.delete(food);
        FoodTagService.decrement(food);
        SelfFoodImageDaoService.deleteByFood(food);
        SelfFoodTagDaoService.deleteByFood(food);
        foodDeletedListeners.forEach(l -> l.accept(food));
    }

    public static void updateFood(Food food){
        long foodId = food.getId();
        SelfFoodDaoService.update(food);
        Food existing = selectById(foodId);
        if (existing != null){
            FoodTagService.decrement(existing);
            SelfFoodImageDaoService.deleteByFood(existing);
            SelfFoodTagDaoService.deleteByFood(food);
        }
        FoodTagService.increment(food);
        SelfFoodImageDaoService.insert(foodId, food.getImages());
        SelfFoodTagDaoService.insert(foodId, food.getTags());
    }

    public static Food selectById(long id){
        Food food = SelfFoodDaoService.selectById(id);
        if (food == null){
            return null;
        }
        fillFood(food);
        return food;
    }

    public static Food selectByName(String name){
        Food food = SelfFoodDaoService.selectByName(name);
        if (food == null){
            return null;
        }
        fillFood(food);
        return food;
    }

    private static void fillFood(Food food){
        long id = food.getId();
        food.setTags(new AList<>(FoodTagService.selectByFood(id)));
        AList<String> images = new AList<>(SelfFoodImageDaoService.selectByFood(id));
        food.setImages(images);
    }

    public static List<Food> selectByTag(Tag tag){
        List<Long> foodIds = SelfFoodTagDaoService.selectByTag(tag.getId()).stream()
                .map(SelfFoodTagDAO::getFoodId)
                .collect(Collectors.toList());
        return SelfFoodDaoService.selectByIds(foodIds);
    }

    public static void deleteNonExistentImage(List<String> currentImages){
        SelfFoodImageDaoService.clearNonExistent(currentImages);
        // TODO clear food cover
    }

    public static List<Food> getFavoriteFoods(){
        List<Food> foods = SelfFoodDaoService.getFavoriteFoods();
        for (Food food : foods){
            food.setTags(new AList<>(FoodTagService.selectByFood(food.getId())));
        }
        return foods;
    }
}
