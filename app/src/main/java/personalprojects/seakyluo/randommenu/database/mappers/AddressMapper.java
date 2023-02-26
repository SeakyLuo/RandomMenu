package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;

@Dao
public interface AddressMapper {

    @Insert
    void insert(List<AddressDAO> list);

    @Delete
    int delete(AddressDAO dao);

    @Query("select * from ADDRESS where restaurantId = :restaurantId order by `order`")
    List<AddressDAO> selectByRestaurant(int restaurantId);
}
