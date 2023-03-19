package personalprojects.seakyluo.randommenu.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.services.FoodTagDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodTagDaoService;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;

public class FoodTagService {

    public static void increment(Food food){
        increment(food.getId(), food.getTags());
    }

    public static void increment(long foodId, List<Tag> tags){
        for (int i = 0; i < tags.size(); i++){
            Tag tag = tags.get(i);
            Tag existing = FoodTagDaoService.selectByName(tag.getName());
            if (existing == null){
                tag.setCounter(1);
                FoodTagDaoService.insert(tag);
            } else {
                tag.setCounter(existing.getCounter());
                existing.more();
                FoodTagDaoService.update(existing);
            }
        }
        SelfFoodTagDaoService.insert(foodId, tags);
    }

    public static void decrement(Food food){
        decrement(food.getId(), food.getTags());
    }

    public static void decrement(long foodId, List<Tag> tags){
        for (Tag tag : tags){
            tag.less();
            SelfFoodTagDaoService.deleteByFoodAndTag(foodId, tag);
            if (tag.getCounter() == 0){
                FoodTagDaoService.delete(tag);
            } else {
                FoodTagDaoService.update(tag);
            }
        }
    }

    public static void delete(Tag tag){
        FoodTagDaoService.delete(tag);
        SelfFoodTagDaoService.deleteByTag(tag);
    }

    public static List<Tag> selectByFood(long foodId){
        List<SelfFoodTagDAO> selfFoodTagDAOS = SelfFoodTagDaoService.selectByFood(foodId);
        List<Long> tagIds = selfFoodTagDAOS.stream().map(SelfFoodTagDAO::getTagId).collect(Collectors.toList());
        return FoodTagDaoService.selectByIds(tagIds);
    }

}
