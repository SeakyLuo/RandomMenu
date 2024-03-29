package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "address")
public class AddressDAO {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(index = true)
    private long restaurantId;
    private String province;
    private String city;
    private String county;
    private String address;
    private int order;
}
