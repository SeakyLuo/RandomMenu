package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.FoodTypeDAO;

@Dao
public interface FoodTypeMapper {

    @Insert
    long insert(FoodTypeDAO dao);

    @Update
    void update(FoodTypeDAO dao);

    @Delete
    int delete(FoodTypeDAO dao);

    @Query("select * from food_type")
    List<FoodTypeDAO> selectAll();

    @Query("select * from food_type where id = :id")
    FoodTypeDAO selectById(long id);
}
