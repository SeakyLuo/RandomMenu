package personalprojects.seakyluo.randommenu.models;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import personalprojects.seakyluo.randommenu.enums.RestaurantOrderByField;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestaurantFilter extends PagerFilter<RestaurantVO> {

    private Long startTime;
    private Long endTime;
    private FoodType foodType;
    private AddressVO address;
    private List<String> eaters;
    private List<RestaurantOrderByField> orderByDesc;

    public boolean isEmpty(){
        return startTime == null && endTime == null && foodType == null && address == null && eaters == null && CollectionUtils.isEmpty(orderByDesc);
    }

}
