package personalprojects.seakyluo.randommenu.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.dao.ImagePathDAO;
import personalprojects.seakyluo.randommenu.database.dao.SelfMadeFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodTagDaoService;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfMadeFoodService {

    private static List<Consumer<SelfMadeFood>> foodDeletedListeners = new ArrayList<>();

    public static void addFood(SelfMadeFood food){
        SelfMadeFood existing = SelfFoodDaoService.selectByName(food.getName());
        if (existing != null){
            return;
        }
        SelfFoodDaoService.insert(food);
        FoodTagService.increment(food);
        long id = food.getId();
        ImagePathService.insertSelfMadeFood(id, food.getImages());
        SelfFoodTagDaoService.insert(id, food.getTags());
    }

    public static void removeFood(SelfMadeFood food){
        SelfFoodDaoService.delete(food);
        FoodTagService.decrement(food);
        ImagePathService.deleteBySelfMadeFood(food);
        SelfFoodTagDaoService.deleteByFood(food);
        foodDeletedListeners.forEach(l -> l.accept(food));
    }

    public static void updateFood(SelfMadeFood food){
        long foodId = food.getId();
        SelfFoodDaoService.update(food);
        SelfMadeFood existing = selectById(foodId);
        if (existing != null){
            FoodTagService.decrement(existing);
            ImagePathService.deleteBySelfMadeFood(food);
            SelfFoodTagDaoService.deleteByFood(food);
        }
        FoodTagService.increment(food);
        ImagePathService.insertSelfMadeFood(foodId, food.getImages());
        SelfFoodTagDaoService.insert(foodId, food.getTags());
    }

    public static List<SelfMadeFood> selectAll(){
        return SelfFoodDaoService.selectAll().parallelStream()
                .peek(SelfMadeFoodService::fillFood)
                .collect(Collectors.toList());
    }

    public static SelfMadeFood selectById(Long id){
        if (id == null){
            return null;
        }
        SelfMadeFood food = SelfFoodDaoService.selectById(id);
        if (food == null){
            return null;
        }
        fillFood(food);
        return food;
    }

    public static SelfMadeFood selectByName(String name){
        SelfMadeFood food = SelfFoodDaoService.selectByName(name);
        if (food == null){
            return null;
        }
        fillFood(food);
        return food;
    }

    private static void fillFood(SelfMadeFood food){
        long id = food.getId();
        food.setTags(new AList<>(FoodTagService.selectByFood(id)));
        AList<String> images = new AList<>(ImagePathService.selectBySelfMadeFood(id));
        food.setImages(images);
    }

    public static List<SelfMadeFood> selectByTag(Tag tag){
        List<Long> foodIds = SelfFoodTagDaoService.selectByTag(tag.getId()).stream()
                .map(SelfMadeFoodTagDAO::getFoodId)
                .distinct()
                .collect(Collectors.toList());
        List<SelfMadeFood> foods = SelfFoodDaoService.selectByIds(foodIds);
        foods.sort(Comparator.comparingLong(SelfMadeFood::getId).reversed());
        return foods;
    }

    public static List<SelfMadeFood> getFavoriteFoods(){
        List<SelfMadeFood> foods = SelfFoodDaoService.getFavoriteFoods();
        for (SelfMadeFood food : foods){
            food.setTags(new AList<>(FoodTagService.selectByFood(food.getId())));
        }
        return foods;
    }
}
