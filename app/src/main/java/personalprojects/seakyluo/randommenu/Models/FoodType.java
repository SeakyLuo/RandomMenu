package personalprojects.seakyluo.randommenu.models;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Data;

@Data
public class FoodType {

    private String name;
    private String code;

    public static String getNameByCode(String code){
        return initFoodTypeMap().get(code);
    }

    public static String getCodeByName(String name){
        return initFoodTypeMap().inverse().get(name);
    }

    private static BiMap<String, String> initFoodTypeMap(){
        if (FOOD_TYPE_MAP != null){
            return FOOD_TYPE_MAP;
        }
        FOOD_TYPE_MAP = HashBiMap.create();
        Settings.settings.FoodTypes.forEach(t -> FOOD_TYPE_MAP.put(t.getCode(), t.getName()));
        return FOOD_TYPE_MAP;
    }

    private static BiMap<String, String> FOOD_TYPE_MAP = null;
}