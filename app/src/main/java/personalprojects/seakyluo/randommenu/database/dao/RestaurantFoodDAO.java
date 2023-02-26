package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "restaurant_food")
public class RestaurantFoodDAO {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(index = true)
    private int restaurantId;
    @ColumnInfo(index = true)
    private int consumeRecordId;
    private String name;
    private String pictureUri;
    private String comment;
    private double price;
    private int order;

}
