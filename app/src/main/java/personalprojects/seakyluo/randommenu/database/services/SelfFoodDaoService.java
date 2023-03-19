package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodMapper;
import personalprojects.seakyluo.randommenu.models.Food;

public class SelfFoodDaoService {

    public static void insert(Food food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        Long id = selfFoodMapper.insert(convert(food));
        food.setId(id);
    }

    public static void delete(Food food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.delete(convert(food));
    }

    public static void update(Food food){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.update(convert(food));
    }

    public static Food selectById(long id){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return convert(selfFoodMapper.selectById(id));
    }

    public static List<Food> selectByIds(List<Long> ids){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectByIds(ids).stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static Food selectByName(String name){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return convert(selfFoodMapper.selectByName(name));
    }

    public static List<Food> selectAll(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectAll().stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static List<Food> selectNonHidden(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectNonHidden().stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static long count(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.count();
    }

    public static List<Food> getFavoriteFoods(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.selectFavorite(true).stream()
                .map(SelfFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static long countFavoriteFoods(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        return selfFoodMapper.countFavorite(true);
    }

    public static void decrementHideCount(){
        SelfFoodMapper selfFoodMapper = AppDatabase.instance.selfFoodMapper();
        selfFoodMapper.decrementHideCount();
    }

    private static SelfFoodDAO convert(Food src){
        if (src == null){
            return null;
        }
        SelfFoodDAO dst = new SelfFoodDAO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setNote(src.getNote());
        dst.setFavorite(src.isFavorite());
        dst.setFoodCover(src.getCover());
        dst.setDateAdded(src.getDateAdded());
        dst.setHideCount(src.getHideCount());
        return dst;
    }

    private static Food convert(SelfFoodDAO src){
        if (src == null){
            return null;
        }
        Food dst = new Food();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setNote(src.getNote());
        dst.setFavorite(src.isFavorite());
        dst.setCover(src.getFoodCover());
        dst.setDateAdded(src.getDateAdded());
        dst.setHideCount(src.getHideCount());
        return dst;
    }
}
