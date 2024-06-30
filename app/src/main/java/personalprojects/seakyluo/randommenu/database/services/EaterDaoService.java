package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.EaterDAO;
import personalprojects.seakyluo.randommenu.models.EaterCount;

public class EaterDaoService {

    public static List<EaterCount> getCount(){
        return AppDatabase.instance.eaterMapper().getEaterCount();
    }

    public static void insert(long restaurantId, long consumeRecordId, List<String> friends){
        List<EaterDAO> daoList = friends.stream().map(f -> {
            EaterDAO dao = new EaterDAO();
            dao.setRestaurantId(restaurantId);
            dao.setConsumeRecordId(consumeRecordId);
            dao.setEater(f);
            return dao;
        }).collect(Collectors.toList());
        AppDatabase.instance.eaterMapper().insert(daoList);
    }

    public static void delete(long restaurantId, long consumeRecordId){
        AppDatabase.instance.eaterMapper().delete(restaurantId, consumeRecordId);
    }

    public static void deleteByConsumeRecords(long restaurantId, List<Long> consumeRecordIds){
        AppDatabase.instance.eaterMapper().deleteByConsumeRecords(restaurantId, consumeRecordIds);
    }
}