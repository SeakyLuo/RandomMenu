package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "auto_tag_map")
public class TagMapEntryDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String keyword;
    private String tags;

}
