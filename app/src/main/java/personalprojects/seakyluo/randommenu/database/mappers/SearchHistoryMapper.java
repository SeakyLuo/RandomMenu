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

    @Query("delete from SEARCH_HISTORY where keyword = :keyword and searchType = :searchType")
    int delete(String searchType, String keyword);

    @Query("select * from SEARCH_HISTORY where searchType = :searchType order by id desc")
    List<SearchHistoryDAO> list(String searchType);
}
