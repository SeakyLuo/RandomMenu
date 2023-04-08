package personalprojects.seakyluo.randommenu.models;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestaurantFilter extends PagerFilter<RestaurantVO> {

    private Long startTime;
    private Long endTime;
    private AddressVO address;
    private List<String> eaters;

    public boolean isEmpty(){
        return startTime == null && endTime == null && address == null && eaters == null;
    }

}
