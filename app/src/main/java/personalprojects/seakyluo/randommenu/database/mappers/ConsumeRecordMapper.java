package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.ConsumeRecordDAO;

@Dao
public interface ConsumeRecordMapper {

    @Insert
    List<Long> insert(List<ConsumeRecordDAO> daoList);

    @Insert
    Long insert(ConsumeRecordDAO dao);

    @Update
    void update(ConsumeRecordDAO dao);

    @Delete
    int delete(ConsumeRecordDAO dao);

    @Query("delete from consume_record where restaurantId = :restaurantId")
    void deleteByRestaurant(long restaurantId);

    @Query("select * from consume_record where restaurantId = :restaurantId order by consumeTime desc")
    List<ConsumeRecordDAO> selectByRestaurant(long restaurantId);

    @Query("select * from consume_record where comment like '%' || :keyword || '%'")
    List<ConsumeRecordDAO> search(String keyword);

    @Query("select eaters from consume_record")
    List<String> selectAllEaters();
}
