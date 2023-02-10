package personalprojects.seakyluo.randommenu.models.dao;

import java.util.List;

import lombok.Data;
import personalprojects.seakyluo.randommenu.models.Address;

@Data
public class RestaurantDAO {

    private String name;
    private List<Address> addressList;
    private String foodTypeCode;
    private String comment;
    private String link;
    private List<ConsumeRecordDAO> records;

}
