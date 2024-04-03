package personalprojects.seakyluo.randommenu.database.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "search_history")
public class SearchHistoryDAO {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String keyword;
    private String searchType;

}
