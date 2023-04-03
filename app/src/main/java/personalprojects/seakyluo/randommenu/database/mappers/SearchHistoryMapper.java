package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;
import personalprojects.seakyluo.randommenu.database.dao.SearchHistoryDAO;

@Dao
public interface SearchHistoryMapper {

    @Insert
    Long insert(SearchHistoryDAO dao);

    @Delete
    int delete(SearchHistoryDAO dao);

    @Query("select * from SEARCH_HISTORY where searchType = :searchType")
    List<SearchHistoryDAO> selectByRestaurant(String searchType);
}
