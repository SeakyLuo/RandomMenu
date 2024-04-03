package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "food_tag")
public class FoodTagDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int foodCount;

}
