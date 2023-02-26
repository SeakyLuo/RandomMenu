package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;

@Dao
public interface RestaurantMapper {

    @Insert
    void insert(RestaurantDAO dao);

    @Update
    void update(RestaurantDAO dao);

    @Delete
    int delete(RestaurantDAO dao);

    @Query("SELECT * FROM restaurant order by id desc limit :pageSize offset (:pageNum * :pageSize)")
    List<RestaurantDAO> selectByPage(int pageNum, int pageSize);
}
