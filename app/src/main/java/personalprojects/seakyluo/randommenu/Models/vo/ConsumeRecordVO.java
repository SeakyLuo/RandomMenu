package personalprojects.seakyluo.randommenu.models.vo;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import personalprojects.seakyluo.randommenu.models.Address;

@Data
public class ConsumeRecordVO {

    private String time;
    private Address address;
    private Timestamp consumeTime;
    private List<String> eaters;

}
