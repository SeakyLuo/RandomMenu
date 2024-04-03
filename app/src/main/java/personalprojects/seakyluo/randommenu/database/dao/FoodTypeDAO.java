package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(tableName = "food_type")
@NoArgsConstructor
@AllArgsConstructor
public class FoodTypeDAO {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
}
