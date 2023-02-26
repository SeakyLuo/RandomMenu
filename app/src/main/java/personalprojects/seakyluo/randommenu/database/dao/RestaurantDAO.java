package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "restaurant")
public class RestaurantDAO {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String foodTypeCode;
    private String comment;
    private String link;

}
