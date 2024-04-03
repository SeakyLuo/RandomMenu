package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "restaurant")
public class RestaurantDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    @ColumnInfo(index = true)
    private long foodTypeId;
    private String comment;
    private String link;
    private double averageCost;
    private long firstVisitTime;
    private long lastVisitTime;

}
