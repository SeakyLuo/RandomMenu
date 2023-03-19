package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.SelfFoodImageDAO;
import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;

@Dao
public interface SelfFoodImageMapper {

    @Insert
    List<Long> insert(List<SelfFoodImageDAO> list);

    @Query("delete from self_food_image where foodId = :foodId")
    void deleteByFood(long foodId);

    @Query("select image from self_food_image where foodId = :foodId order by orderNum asc")
    List<String> selectByFood(long foodId);

    @Query("delete from self_food_image where image not in (:existing)")
    void clearNonExistent(List<String> existing);

    @Query("select image from self_food_image")
    List<String> selectPaths();

}
