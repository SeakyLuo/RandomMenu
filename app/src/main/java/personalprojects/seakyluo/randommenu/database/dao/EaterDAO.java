package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "eater")
public class EaterDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(index = true)
    private long restaurantId;
    private long consumeRecordId;
    private String eater;

}
