package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.SelfFoodTagDAO;

@Dao
public interface SelfFoodTagMapper {

    @Insert
    List<Long> insert(List<SelfFoodTagDAO> list);

    @Query("delete from self_food_tag where tagId = :tagId")
    void deleteByTag(long tagId);

    @Query("delete from self_food_tag where foodId = :foodId")
    void deleteByFood(long foodId);

    @Query("delete from self_food_tag where foodId = :foodId and tagId = :tagId")
    void deleteByFoodAndTag(long foodId, long tagId);

    @Query("select * from self_food_tag where foodId = :foodId order by orderNum asc")
    List<SelfFoodTagDAO> selectByFood(long foodId);

    @Query("select * from self_food_tag where tagId = :tagId")
    List<SelfFoodTagDAO> selectByTag(long tagId);

}
