package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.ImagePathDAO;

@Dao
public interface ImagePathMapper {

    @Insert
    List<Long> insert(List<ImagePathDAO> list);

    @Query("delete from image_path where itemId = :itemId and itemType = :itemType")
    void deleteByItem(long itemId, String itemType);

    @Query("delete from image_path where itemId in (:itemIds) and itemType = :itemType")
    void deleteByItems(List<Long> itemIds, String itemType);

    @Query("select path from image_path  where itemId = :itemId and itemType = :itemType order by orderNum asc")
    List<String> selectByItem(long itemId, String itemType);

    @Query("select * from image_path  where itemId in (:itemIds) and itemType = :itemType")
    List<ImagePathDAO> selectByItems(List<Long> itemIds, String itemType);

    @Query("delete from image_path where path not in (:existing)")
    void clearNonExistent(List<String> existing);

    @Query("select * from image_path where path not in (:existing)")
    List<ImagePathDAO> selectNonExistent(List<String> existing);

    @Query("select path from image_path ")
    List<String> selectPaths();

}
