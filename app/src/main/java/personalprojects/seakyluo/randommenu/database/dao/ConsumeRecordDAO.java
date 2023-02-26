package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "consume_record")
public class ConsumeRecordDAO {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(index = true)
    private int restaurantId;
    private long consumeTime;
    private int addressId;
    private String eaters;
    private double totalCost;
    private String comment;
    private int order;

}