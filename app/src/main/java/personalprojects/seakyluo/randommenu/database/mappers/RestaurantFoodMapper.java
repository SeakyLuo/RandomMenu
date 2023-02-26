package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;

@Dao
public interface RestaurantFoodMapper {

    @Insert
    void insert(List<RestaurantFoodDAO> list);

    @Delete
    int delete(RestaurantFoodDAO dao);

    @Query("select * from restaurant_food where restaurantId = :restaurantId")
    List<RestaurantFoodDAO> selectByRestaurant(int restaurantId);

    @Query("select * from restaurant_food where consumeRecordId = :consumeRecordId order by `order`")
    List<RestaurantFoodDAO> selectByConsumeRecord(int consumeRecordId);

}
