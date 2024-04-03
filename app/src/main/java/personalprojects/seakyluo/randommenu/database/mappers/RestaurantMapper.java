package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.Collection;
import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;

@Dao
public interface RestaurantMapper {

    @Insert
    long insert(RestaurantDAO dao);

    @Insert
    List<Long> insert(List<RestaurantDAO> daoList);

    @Update
    void update(RestaurantDAO dao);

    @Delete
    int delete(RestaurantDAO dao);

    @Query("SELECT * FROM restaurant where id = :id")
    RestaurantDAO selectById(long id);

    @Query("SELECT * FROM restaurant where id in (:ids)")
    List<RestaurantDAO> selectByIds(Collection<Long> ids);

    @Query("SELECT * FROM restaurant order by lastVisitTime desc limit :pageSize offset ((:pageNum - 1) * :pageSize)")
    List<RestaurantDAO> selectByPage(int pageNum, int pageSize);

    @RawQuery(observedEntities = RestaurantDAO.class)
    List<Long> filter(SupportSQLiteQuery query);

    @RawQuery(observedEntities = RestaurantDAO.class)
    long selectCountByFilter(SupportSQLiteQuery query);

    @Query("SELECT count(0) FROM restaurant")
    long selectCount();

    @Query("SELECT count(0) FROM restaurant where foodTypeId = :foodTypeId")
    long countByFoodType(long foodTypeId);

    @Query("select * from restaurant where name like '%' || :keyword || '%' or comment like '%' || :keyword || '%'")
    List<RestaurantDAO> search(String keyword);
}
