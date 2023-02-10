package personalprojects.seakyluo.randommenu.converters;

import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.dao.RestaurantDAO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantConverter {

    public static RestaurantVO convert(RestaurantDAO src){
        RestaurantVO dst = new RestaurantVO();
        dst.setName(src.getName());
        dst.setAddressList(src.getAddressList());
        dst.setFoodTypeCode(src.getFoodTypeCode());
        dst.setFoodTypeName(FoodType.getNameByCode(src.getFoodTypeCode()));
        dst.setComment(src.getComment());
        dst.setLink(src.getLink());
        return dst;
    }

}
