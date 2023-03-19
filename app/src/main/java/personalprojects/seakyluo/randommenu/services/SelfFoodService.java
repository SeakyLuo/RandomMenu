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
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfFoodService {

    private static List<Consumer<SelfFood>> foodDeletedListeners = new ArrayList<>();

    public static void addFood(SelfFood food){
        SelfFood existing = SelfFoodDaoService.selectByName(food.getName());
        if (existing != null){
            return;
        }
        SelfFoodDaoService.insert(food);
        FoodTagService.increment(food);
        long id = food.getId();
        SelfFoodImageDaoService.insert(id, food.getImages());
        SelfFoodTagDaoService.insert(id, food.getTags());
    }

    public static void removeFood(SelfFood food){
        SelfFoodDaoService.delete(food);
        FoodTagService.decrement(food);
        SelfFoodImageDaoService.deleteByFood(food);
        SelfFoodTagDaoService.deleteByFood(food);
        foodDeletedListeners.forEach(l -> l.accept(food));
    }

    public static void updateFood(SelfFood food){
        long foodId = food.getId();
        SelfFoodDaoService.update(food);
        SelfFood existing = selectById(foodId);
        if (existing != null){
            FoodTagService.decrement(existing);
            SelfFoodImageDaoService.deleteByFood(existing);
            SelfFoodTagDaoService.deleteByFood(food);
        }
        FoodTagService.increment(food);
        SelfFoodImageDaoService.insert(foodId, food.getImages());
        SelfFoodTagDaoService.insert(foodId, food.getTags());
    }

    public static List<SelfFood> selectAll(){
        return SelfFoodDaoService.selectAll().parallelStream()
                .peek(SelfFoodService::fillFood)
                .collect(Collectors.toList());
    }

    public static SelfFood selectById(long id){
        SelfFood food = SelfFoodDaoService.selectById(id);
        if (food == null){
            return null;
        }
        fillFood(food);
        return food;
    }

    public static SelfFood selectByName(String name){
        SelfFood food = SelfFoodDaoService.selectByName(name);
        if (food == null){
            return null;
        }
        fillFood(food);
        return food;
    }

    private static void fillFood(SelfFood food){
        long id = food.getId();
        food.setTags(new AList<>(FoodTagService.selectByFood(id)));
        AList<String> images = new AList<>(SelfFoodImageDaoService.selectByFood(id));
        food.setImages(images);
    }

    public static List<SelfFood> selectByTag(Tag tag){
        List<Long> foodIds = SelfFoodTagDaoService.selectByTag(tag.getId()).stream()
                .map(SelfFoodTagDAO::getFoodId)
                .collect(Collectors.toList());
        return SelfFoodDaoService.selectByIds(foodIds);
    }

    public static void deleteNonExistentImage(List<String> currentImages){
        SelfFoodImageDaoService.clearNonExistent(currentImages);
        // TODO clear food cover
    }

    public static List<SelfFood> getFavoriteFoods(){
        List<SelfFood> foods = SelfFoodDaoService.getFavoriteFoods();
        for (SelfFood food : foods){
            food.setTags(new AList<>(FoodTagService.selectByFood(food.getId())));
        }
        return foods;
    }
}
