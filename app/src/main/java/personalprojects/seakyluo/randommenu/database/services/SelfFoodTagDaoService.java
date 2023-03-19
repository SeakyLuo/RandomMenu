package personalprojects.seakyluo.randommenu.database.services;

import java.util.ArrayList;
import java.util.List;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodTagMapper;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfFoodTagDaoService {

    public static void insert(long foodId, List<Tag> tags){
        SelfFoodTagMapper mapper = AppDatabase.instance.selfFoodTagMapper();
        List<SelfFoodTagDAO> daoList = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++){
            Tag tag = tags.get(i);
            SelfFoodTagDAO dao = convert(foodId, tag, i);
            daoList.add(dao);
        }
        mapper.insert(daoList);
    }

    public static void deleteByTag(Tag tag){
        SelfFoodTagMapper mapper = AppDatabase.instance.selfFoodTagMapper();
        mapper.deleteByTag(tag.getId());
    }

    public static void deleteByFood(SelfFood food){
        SelfFoodTagMapper mapper = AppDatabase.instance.selfFoodTagMapper();
        mapper.deleteByFood(food.getId());
    }

    public static void deleteByFoodAndTag(long foodId, Tag tag){
        SelfFoodTagMapper mapper = AppDatabase.instance.selfFoodTagMapper();
        mapper.deleteByFoodAndTag(foodId, tag.getId());
    }

    public static List<SelfFoodTagDAO> selectByFood(long foodId){
        SelfFoodTagMapper mapper = AppDatabase.instance.selfFoodTagMapper();
        return mapper.selectByFood(foodId);
    }

    public static List<SelfFoodTagDAO> selectByTag(long tagId){
        SelfFoodTagMapper mapper = AppDatabase.instance.selfFoodTagMapper();
        return mapper.selectByTag(tagId);
    }

    private static SelfFoodTagDAO convert(long foodId, Tag tag, int order){
        SelfFoodTagDAO dao = new SelfFoodTagDAO();
        dao.setFoodId(foodId);
        dao.setTagId(tag.getId());
        dao.setOrderNum(order);
        return dao;
    }
}
