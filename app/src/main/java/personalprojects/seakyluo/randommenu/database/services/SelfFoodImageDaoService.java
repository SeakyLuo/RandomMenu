package personalprojects.seakyluo.randommenu.database.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodImageDAO;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodImageMapper;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodTagMapper;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;

public class SelfFoodImageDaoService {

    public static void insert(long foodId, List<String> images){
        SelfFoodImageMapper mapper = AppDatabase.instance.selfFoodImageMapper();
        List<SelfFoodImageDAO> daoList = new ArrayList<>();
        for (int i = 0; i < images.size(); i++){
            String image = images.get(i);
            SelfFoodImageDAO dao = convert(foodId, image, i);
            daoList.add(dao);
        }
        mapper.insert(daoList);
    }

    public static void deleteByFood(Food food){
        SelfFoodImageMapper mapper = AppDatabase.instance.selfFoodImageMapper();
        mapper.deleteByFood(food.getId());
    }

    public static List<String> selectByFood(long foodId){
        SelfFoodImageMapper mapper = AppDatabase.instance.selfFoodImageMapper();
        return mapper.selectByFood(foodId);
    }

    public static void clearNonExistent(List<String> existing){
        SelfFoodImageMapper mapper = AppDatabase.instance.selfFoodImageMapper();
        mapper.clearNonExistent(existing);
    }

    public static List<String> selectPaths(){
        SelfFoodImageMapper mapper = AppDatabase.instance.selfFoodImageMapper();
        return mapper.selectPaths();
    }

    private static SelfFoodImageDAO convert(long foodId, String image, int order){
        SelfFoodImageDAO dao = new SelfFoodImageDAO();
        dao.setFoodId(foodId);
        dao.setImage(image);
        dao.setOrderNum(order);
        return dao;
    }
}
