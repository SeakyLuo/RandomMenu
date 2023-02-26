package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "address")
public class AddressDAO {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(index = true)
    private int restaurantId;
    private String province;
    private String city;
    private String county;
    private String address;
    private int order;
}
