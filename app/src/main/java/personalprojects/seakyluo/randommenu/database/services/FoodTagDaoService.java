package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.FoodTagDAO;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTagMapper;
import personalprojects.seakyluo.randommenu.models.Tag;

public class FoodTagDaoService {

    public static void insert(Tag tag){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        Long id = foodTagMapper.insert(convert(tag));
        tag.setId(id);
    }

    public static void delete(Tag tag){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        foodTagMapper.delete(convert(tag));
    }

    public static Tag update(Tag tag){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        foodTagMapper.update(convert(tag));
        return tag;
    }

    public static Tag selectByName(String tagName){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        FoodTagDAO dao = foodTagMapper.selectByName(tagName);
        return convert(dao);
    }

    public static Tag selectById(long id){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        FoodTagDAO dao = foodTagMapper.selectById(id);
        return convert(dao);
    }

    public static List<Tag> selectByIds(List<Long> ids){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        return foodTagMapper.selectByIds(ids).stream()
                .map(FoodTagDaoService::convert)
                .collect(Collectors.toList());
    }

    public static List<Tag> selectAll(){
        FoodTagMapper foodTagMapper = AppDatabase.instance.foodTagMapper();
        return foodTagMapper.selectAll().stream().map(FoodTagDaoService::convert).collect(Collectors.toList());
    }

    private static Tag convert(FoodTagDAO src){
        if (src == null){
            return null;
        }
        Tag dst = new Tag();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setCounter(src.getFoodCount());
        return dst;
    }

    private static FoodTagDAO convert(Tag src){
        if (src == null){
            return null;
        }
        FoodTagDAO dst = new FoodTagDAO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setFoodCount(src.getCounter());
        return dst;
    }
}
