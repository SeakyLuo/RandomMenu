package personalprojects.seakyluo.randommenu.services;

import java.util.List;
import java.util.Map;

import personalprojects.seakyluo.randommenu.database.services.ImagePathDaoService;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;

public class ImagePathService {

    public static void insertSelfMadeFood(long itemId, List<String> images){
        ImagePathDaoService.insert(itemId, FoodClass.SELF_MADE.name(), images);
    }

    public static void insertRestaurantFood(long itemId, List<String> images){
        ImagePathDaoService.insert(itemId, FoodClass.RESTAURANT.name(), images);
    }

    public static void deleteBySelfMadeFood(SelfMadeFood food){
        ImagePathDaoService.deleteByItem(food.getId(), FoodClass.SELF_MADE.name());
    }

    public static void deleteByRestaurantFoods(List<Long> foodIds){
        ImagePathDaoService.deleteByItems(foodIds, FoodClass.RESTAURANT.name());
    }

    public static List<String> selectBySelfMadeFood(long foodId){
        return ImagePathDaoService.selectByItem(foodId, FoodClass.SELF_MADE.name());
    }

    public static List<String> selectByRestaurantFood(long foodId){
        return ImagePathDaoService.selectByItem(foodId, FoodClass.RESTAURANT.name());
    }

    public static Map<Long, List<String>> selectByRestaurantFoods(List<Long> foodIds){
        return ImagePathDaoService.selectByItems(foodIds, FoodClass.RESTAURANT.name());
    }

    public static void clearNonExistent(List<String> existing){
        ImagePathDaoService.clearNonExistent(existing);
    }

    public static List<String> selectPaths(){
        return ImagePathDaoService.selectPaths();
    }

}
