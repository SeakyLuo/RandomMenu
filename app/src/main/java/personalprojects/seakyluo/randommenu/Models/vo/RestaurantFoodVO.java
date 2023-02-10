package personalprojects.seakyluo.randommenu.models.vo;

import java.util.List;

import lombok.Data;

@Data
public class RestaurantFoodVO {

    private String name;
    private String pictureUri;
    private String description;
    private double price;

}