package personalprojects.seakyluo.randommenu.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTagMapper;
import personalprojects.seakyluo.randommenu.database.services.FoodTagDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodTagDaoService;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class FoodTagService {

    public static boolean insert(Tag tag){
        Tag existing = FoodTagDaoService.selectByName(tag.getName());
        if (existing != null){
            tag.setId(existing.getId());
            return false;
        }
        FoodTagDaoService.insert(tag);
        return true;
    }

    public static void update(Tag tag){
        long id = tag.getId();
        Tag existing = FoodTagDaoService.selectById(id);
        FoodTagDaoService.update(tag);
        String oldName = existing.getName();
        String newName = tag.getName();
        if (oldName.equals(newName)){
            return;
        }
        List<SelfFood> foods = SelfFoodService.selectByTag(tag);
        for (SelfFood food : foods){
            for (Tag t : food.getTags()){
                if (t.getId() == id){
                    t.setName(newName);
                    break;
                }
            }
            SelfFoodDaoService.update(food);
        }
    }

    public static void increment(SelfFood food){
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

    public static void decrement(SelfFood food){
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
