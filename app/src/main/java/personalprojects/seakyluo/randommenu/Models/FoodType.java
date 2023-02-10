package personalprojects.seakyluo.randommenu.models;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class FoodType {

    private String name;
    private String code;

    public static String getNameByCode(String code){
        if (FOOD_TYPE_MAP == null){
            FOOD_TYPE_MAP = Settings.settings.FoodTypes.stream().collect(Collectors.toMap(FoodType::getCode, FoodType::getName));
        }
        return FOOD_TYPE_MAP.get(code);
    }

    private static Map<String, String> FOOD_TYPE_MAP = null;
}