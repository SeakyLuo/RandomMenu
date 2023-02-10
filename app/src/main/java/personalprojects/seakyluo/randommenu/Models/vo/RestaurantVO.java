package personalprojects.seakyluo.randommenu.models.vo;

import java.util.List;

import lombok.Data;
import personalprojects.seakyluo.randommenu.models.Address;

@Data
public class RestaurantVO {

    private String name;
    private List<Address> addressList;
    private String foodTypeCode;
    private String foodTypeName;
    private String comment;
    private String link;
    private List<RestaurantFoodVO> foods;

}
