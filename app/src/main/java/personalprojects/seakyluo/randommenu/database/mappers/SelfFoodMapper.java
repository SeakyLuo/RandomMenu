package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.SelfFoodDAO;

@Dao
public interface SelfFoodMapper {

    @Insert
    Long insert(SelfFoodDAO dao);

    @Update
    void update(SelfFoodDAO dao);

    @Delete
    void delete(SelfFoodDAO dao);

    @Query("select * from self_food where id = :id")
    SelfFoodDAO selectById(long id);

    @Query("select * from self_food where id in (:ids)")
    List<SelfFoodDAO> selectByIds(List<Long> ids);

    @Query("select * from self_food where name = :name")
    SelfFoodDAO selectByName(String name);

    @Query("select * from self_food order by id desc")
    List<SelfFoodDAO> selectAll();

    @Query("select * from self_food where favorite = :isFavorite")
    List<SelfFoodDAO> selectFavorite(boolean isFavorite);

    @Query("select * from self_food where hideCount = 0")
    List<SelfFoodDAO> selectNonHidden();

    @Query("select count(0) from self_food")
    long count();

    @Query("select count(0) from self_food where favorite = :isFavorite")
    long countFavorite(boolean isFavorite);

    @Query("update self_food set hideCount = hideCount - 1 where hideCount > 0")
    void decrementHideCount();
}
