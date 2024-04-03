package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.FoodTagDAO;

@Dao
public interface FoodTagMapper {

    @Insert
    Long insert(FoodTagDAO dao);

    @Update
    void update(FoodTagDAO dao);

    @Delete
    void delete(FoodTagDAO dao);

    @Query("select * from food_tag where id = :id")
    FoodTagDAO selectById(long id);

    @Query("select * from food_tag where id in (:ids)")
    List<FoodTagDAO> selectByIds(List<Long> ids);

    @Query("select * from food_tag where name = :name")
    FoodTagDAO selectByName(String name);

    @Query("select * from food_tag order by foodCount desc")
    List<FoodTagDAO> selectAll();

}
