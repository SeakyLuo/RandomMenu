package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.EaterDAO;
import personalprojects.seakyluo.randommenu.models.EaterCount;

@Dao
public interface EaterMapper {

    @Insert
    List<Long> insert(List<EaterDAO> list);

    @Query("delete from eater where restaurantId = :restaurantId and consumeRecordId = :consumeRecordId")
    int delete(long restaurantId, long consumeRecordId);

    @Query("delete from eater where restaurantId = :restaurantId and consumeRecordId in (:consumeRecordIds)")
    int deleteByConsumeRecords(long restaurantId, List<Long> consumeRecordIds);

    @Query("SELECT eater, COUNT(*) AS count FROM eater GROUP BY eater ORDER BY count DESC")
    List<EaterCount> getEaterCount();

}
