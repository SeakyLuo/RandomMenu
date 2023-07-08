package personalprojects.seakyluo.randommenu.services;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

import personalprojects.seakyluo.randommenu.database.dao.ImagePathDAO;
import personalprojects.seakyluo.randommenu.database.services.ConsumeRecordDaoService;
import personalprojects.seakyluo.randommenu.database.services.ImagePathDaoService;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.database.services.RestaurantFoodDaoService;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

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
        List<ImagePathDAO> paths = ImagePathDaoService.clearNonExistent(existing);
        for (ImagePathDAO dao : paths){
            String path = dao.getPath();
            long itemId = dao.getItemId();
            String itemType = dao.getItemType();
            if (FoodClass.RESTAURANT.name().equals(itemType)){
                RestaurantFoodVO food = RestaurantFoodDaoService.selectById(itemId);
                if (path.equals(food.getCover())){
                    food.setCover(CollectionUtils.isEmpty(food.getImages()) ? null : food.getImages().get(0));
                    RestaurantFoodDaoService.update(food);
                }
            }
            else if (CONSUME_RECORD.equals(itemType)){

            }
            else if (FoodClass.SELF_MADE.name().equals(itemType)){
                SelfMadeFood food = SelfMadeFoodService.selectById(itemId);
                if (path.equals(food.getCover())){
                    food.setCover(food.hasImage() ? food.getImages().get(0) : null);
                    SelfMadeFoodService.updateFood(food);
                }
            }
        }
    }

    public static List<String> selectPaths(){
        return ImagePathDaoService.selectPaths();
    }

}
