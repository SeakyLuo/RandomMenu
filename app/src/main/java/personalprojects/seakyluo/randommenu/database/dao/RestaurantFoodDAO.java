package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "restaurant_food")
public class RestaurantFoodDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(index = true)
    private long restaurantId;
    @ColumnInfo(index = true)
    private long consumeRecordId;
    private String name;
    private String pictureUri;
    private String comment;
    private double price;
    private int order;
    private Boolean showInList = false;
    private Integer orderInHome = -1;

}
