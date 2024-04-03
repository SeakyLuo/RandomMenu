package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.TagMapEntryDAO;

@Dao
public interface TagMapEntryMapper {

    @Insert
    Long insert(TagMapEntryDAO tag);

    @Update
    void update(TagMapEntryDAO dao);

    @Delete
    void delete(TagMapEntryDAO dao);

    @Query("select * from auto_tag_map order by id desc")
    List<TagMapEntryDAO> selectAll();

    @Query("select * from auto_tag_map where keyword = :keyword")
    TagMapEntryDAO selectByKeyword(String keyword);
}
