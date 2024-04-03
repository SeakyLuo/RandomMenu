package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity(tableName = "self_food")
public class SelfMadeFoodDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String note;
    private boolean favorite;
    @Deprecated
    private int cover;
    private String tags;
    private String foodCover;
    private long dateAdded;
    private int hideCount;

}
