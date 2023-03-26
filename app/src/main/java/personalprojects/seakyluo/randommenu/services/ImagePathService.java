package personalprojects.seakyluo.randommenu.services;

import java.util.List;
import java.util.Map;

import personalprojects.seakyluo.randommenu.database.services.ImagePathDaoService;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;

public class ImagePathService {

    private static final String CONSUME_RECORD = "CONSUME_RECORD";

    public static void insertSelfMadeFood(long itemId, List<String> images){
        ImagePathDaoService.insert(itemId, FoodClass.SELF_MADE.name(), images);
    }

    public static void insertRestaurantFood(long itemId, List<String> images){
        ImagePathDaoService.insert(itemId, FoodClass.RESTAURANT.name(), images);
    }

    public static void insertConsumeRecord(long itemId, List<String> images){
        ImagePathDaoService.insert(itemId, CONSUME_RECORD, images);
    }

    public static void deleteBySelfMadeFood(SelfMadeFood food){
        ImagePathDaoService.deleteByItem(food.getId(), FoodClass.SELF_MADE.name());
    }

    public static void deleteByConsumeRecords(List<Long> foodIds){
        ImagePathDaoService.deleteByItems(foodIds, CONSUME_RECORD);
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

    public static List<String> selectByConsumeRecord(long consumeRecordId){
        return ImagePathDaoService.selectByItem(consumeRecordId, CONSUME_RECORD);
    }

    public static Map<Long, List<String>> selectByConsumeRecords(List<Long> consumeRecordIds){
        return ImagePathDaoService.selectByItems(consumeRecordIds, CONSUME_RECORD);
    }


    public static void clearNonExistent(List<String> existing){
        ImagePathDaoService.clearNonExistent(existing);
    }

    public static List<String> selectPaths(){
        return ImagePathDaoService.selectPaths();
    }

}
