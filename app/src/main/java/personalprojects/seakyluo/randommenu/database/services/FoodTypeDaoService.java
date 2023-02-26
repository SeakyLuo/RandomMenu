package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.FoodTypeDAO;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTypeMapper;
import personalprojects.seakyluo.randommenu.models.FoodType;

public class FoodTypeDaoService {

    public static void insert(FoodType item){
        FoodTypeMapper mapper = AppDatabase.instance.foodTypeMapper();
        long id = mapper.insert(convert(item));
        item.setId(id);
    }

    public static void delete(FoodType item){
        FoodTypeMapper mapper = AppDatabase.instance.foodTypeMapper();
        mapper.delete(convert(item));
        // 暂时不从ID_NAME_MAP删除
    }

    public static List<FoodType> selectAll(){
        FoodTypeMapper mapper = AppDatabase.instance.foodTypeMapper();
        return mapper.selectAll().stream().map(FoodTypeDaoService::convert).collect(Collectors.toList());
    }

    public static FoodType selectById(long id){
        FoodTypeMapper mapper = AppDatabase.instance.foodTypeMapper();
        return convert(mapper.selectById(id));
    }

    private static FoodType convert(FoodTypeDAO src){
        return src == null ? null : new FoodType(src.getId(), src.getName());
    }

    private static FoodTypeDAO convert(FoodType src){
        return src == null ? null : new FoodTypeDAO(src.getId(), src.getName());
    }
}
