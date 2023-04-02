package personalprojects.seakyluo.randommenu.database.mappers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;
import personalprojects.seakyluo.randommenu.models.RestaurantFilter;

@Dao
public interface RestaurantMapper {

    @Insert
    long insert(RestaurantDAO dao);

    @Insert
    List<Long> insert(List<RestaurantDAO> daoList);

    @Update
    void update(RestaurantDAO dao);

    @Delete
    int delete(RestaurantDAO dao);

    @Query("SELECT * FROM restaurant where id = :id")
    RestaurantDAO selectById(long id);

    @Query("SELECT * FROM restaurant where id in (:ids)")
    List<RestaurantDAO> selectByIds(List<Long> ids);

    @Query("SELECT * FROM restaurant order by lastVisitTime desc limit :pageSize offset ((:pageNum - 1) * :pageSize)")
    List<RestaurantDAO> selectByPage(int pageNum, int pageSize);

    @Query("SELECT distinct restaurant.id FROM restaurant " +
            "left join consume_record cr on restaurant.id = cr.restaurantId and (:startTime is not null or :endTime is not null) " +
            "left join ADDRESS address on restaurant.id = address.restaurantId and (:province is not null or :city is not null or :county is not null)" +
            "where (:startTime is null or cr.consumeTime >= :startTime) " +
            "AND (:endTime is null or cr.consumeTime <= :endTime) " +
            "AND (" +
            "    (:province IS NULL OR address.province = :province) " +
            "    AND (:city IS NULL OR address.city = :city)" +
            "    AND (:county IS NULL OR address.county = :county) " +
            "    AND (" +
            "        :county IS NOT NULL AND address.county = :county " +
            "        OR " +
            "        (:county IS NULL AND :city IS NOT NULL AND address.city = :city)" +
            "        OR " +
            "        (:county IS NULL AND :city IS NULL AND :province IS NOT NULL AND address.province = :province)" +
            "    ) " +
            ") " +
            "order by restaurant.lastVisitTime desc " +
            "limit :pageSize " +
            "offset ((:pageNum - 1) * :pageSize)")
    List<Long> selectByPageWithFilter(int pageNum, int pageSize,
                                      Long startTime, Long endTime,
                                      String province, String city, String county);

    @Query("SELECT count(0) FROM restaurant")
    long selectCount();

    @Query("SELECT count(0) FROM restaurant where foodTypeId = :foodTypeId")
    long countByFoodType(long foodTypeId);

    @Query("SELECT count(distinct restaurant.id) FROM restaurant " +
            "left join consume_record cr on restaurant.id = cr.restaurantId and (:startTime is not null or :endTime is not null) " +
            "left join ADDRESS address on restaurant.id = address.restaurantId and (:province is not null or :city is not null or :county is not null)" +
            "where (:startTime is null or cr.consumeTime >= :startTime) " +
            "AND (:endTime is null or cr.consumeTime <= :endTime) " +
            "AND (" +
            "    (:province IS NULL OR address.province = :province) " +
            "    AND (:city IS NULL OR address.city = :city)" +
            "    AND (:county IS NULL OR address.county = :county) " +
            "    AND (" +
            "        :county IS NOT NULL AND address.county = :county " +
            "        OR " +
            "        (:county IS NULL AND :city IS NOT NULL AND address.city = :city)" +
            "        OR " +
            "        (:county IS NULL AND :city IS NULL AND :province IS NOT NULL AND address.province = :province)" +
            "    ) " +
            ") ")
    long selectCountByFilter(Long startTime, Long endTime,
                             String province, String city, String county);
}
