package personalprojects.seakyluo.randommenu.services;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.FoodTypeDAO;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTypeMapper;
import personalprojects.seakyluo.randommenu.database.services.FoodTypeDaoService;
import personalprojects.seakyluo.randommenu.models.FoodType;

public class FoodTypeService {

    private static BiMap<Long, String> ID_NAME_MAP = HashBiMap.create();

    private static BiMap<Long, String> getIdNameMap(){
        if (!ID_NAME_MAP.isEmpty()){
            return ID_NAME_MAP;
        }
        for (FoodType foodType : FoodTypeDaoService.selectAll()) {
            ID_NAME_MAP.put(foodType.getId(), foodType.getName());
        }
        return ID_NAME_MAP;
    }

    public static void save(FoodType item){
        if (item.getId() != 0){
            return;
        }
        FoodTypeDaoService.insert(item);
        getIdNameMap().put(item.getId(), item.getName());
    }

    public static void delete(FoodType item){
        FoodTypeDaoService.delete(item);
        // 暂时不从ID_NAME_MAP删除
    }

    public static List<String> selectAllNames(){
        return new ArrayList<>(getIdNameMap().values());
    }

    public static String getNameById(long id){
        return getIdNameMap().get(id);
    }

    public static Long getIdByName(String name){
        return getIdNameMap().inverse().getOrDefault(name, 0L);
    }

}
