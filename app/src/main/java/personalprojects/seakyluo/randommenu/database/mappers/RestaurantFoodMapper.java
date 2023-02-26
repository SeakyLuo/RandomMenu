package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;

@Dao
public interface RestaurantFoodMapper {

    @Insert
    List<Long> insert(List<RestaurantFoodDAO> list);

    @Update
    void update(RestaurantFoodDAO dao);

    @Delete
    int delete(RestaurantFoodDAO dao);

    @Query("select * from restaurant_food where restaurantId = :restaurantId")
    List<RestaurantFoodDAO> selectByRestaurant(long restaurantId);

    @Query("delete from restaurant_food where restaurantId = :restaurantId")
    void deleteByRestaurant(long restaurantId);

    @Query("select * from restaurant_food where consumeRecordId = :consumeRecordId order by `order`")
    List<RestaurantFoodDAO> selectByConsumeRecord(long consumeRecordId);

    @Query("delete from restaurant_food where consumeRecordId = :consumeRecordId")
    void deleteByConsumeRecord(long consumeRecordId);
}
