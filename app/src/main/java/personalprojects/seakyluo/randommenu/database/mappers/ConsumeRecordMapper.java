package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.ConsumeRecordDAO;

@Dao
public interface ConsumeRecordMapper {

    @Insert
    void insert(List<ConsumeRecordDAO> daoList);

    @Delete
    int delete(ConsumeRecordDAO dao);

    @Query("select * from consume_record where restaurantId = :restaurantId order by `order`")
    List<ConsumeRecordDAO> selectByRestaurant(int restaurantId);
}
