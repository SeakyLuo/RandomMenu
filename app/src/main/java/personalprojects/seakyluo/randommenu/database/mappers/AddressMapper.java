package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;

@Dao
public interface AddressMapper {

    @Insert
    List<Long> insert(List<AddressDAO> list);

    @Update
    void update(AddressDAO dao);

    @Delete
    int delete(AddressDAO dao);

    @Query("delete from ADDRESS where restaurantId = :restaurantId")
    void deleteByRestaurant(long restaurantId);

    @Query("select * from ADDRESS where restaurantId = :restaurantId order by `order`")
    List<AddressDAO> selectByRestaurant(long restaurantId);
}
