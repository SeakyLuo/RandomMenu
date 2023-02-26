package personalprojects.seakyluo.randommenu.database.services;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.ConsumeRecordDAO;
import personalprojects.seakyluo.randommenu.database.mappers.ConsumeRecordMapper;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;

public class ConsumeRecordDaoService {

    public static void insert(List<ConsumeRecordVO> voList, int restaurantId, List<Address> addressList){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        List<ConsumeRecordDAO> daoList = voList.stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
        for (int i = 0; i < daoList.size(); i++){
            ConsumeRecordVO vo = voList.get(i);
            ConsumeRecordDAO dao = daoList.get(i);
            dao.setRestaurantId(restaurantId);
            dao.setAddressId(addressList.stream().filter(a -> a.equals(vo.getAddress())).findFirst().map(Address::getId).get());
            dao.setOrder(i);
        }
        mapper.insert(daoList);
        for (int i = 0; i < daoList.size(); i++){
            ConsumeRecordVO vo = voList.get(i);
            ConsumeRecordDAO dao = daoList.get(i);
            int id = dao.getId();
            vo.setId(id);
            RestaurantFoodDaoService.insert(vo.getFoods(), restaurantId, id);
        }
    }

    public static List<ConsumeRecordVO> selectByRestaurant(int restaurantId){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        List<ConsumeRecordVO> records = mapper.selectByRestaurant(restaurantId).stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
        Map<Integer, List<RestaurantFoodVO>> foodMap = RestaurantFoodDaoService.selectByRestaurantId(restaurantId).stream().collect(Collectors.groupingBy(RestaurantFoodVO::getConsumeRecordId));
        Map<Integer, Address> addressMap = AddressDaoService.selectByRestaurant(restaurantId).stream().collect(Collectors.toMap(Address::getId, Function.identity()));
        for (ConsumeRecordVO record : records){
            record.setAddress(addressMap.get(record.getAddress().getId()));
            record.setFoods(foodMap.get(record.getId()));
        }
        return records;
    }

    private static ConsumeRecordDAO convert(ConsumeRecordVO src){
        if (src == null){
            return null;
        }
        ConsumeRecordDAO dst = new ConsumeRecordDAO();
        dst.setId(src.getId());
        dst.setConsumeTime(src.getConsumeTime());
        dst.setAddressId(src.getAddress().getId());
        dst.setEaters(JsonUtils.toJson(src.getEaters()));
        dst.setComment(src.getComment());
        dst.setTotalCost(src.getTotalCost());
        dst.setComment(src.getComment());
        return dst;
    }

    private static ConsumeRecordVO convert(ConsumeRecordDAO src){
        if (src == null){
            return null;
        }
        ConsumeRecordVO dst = new ConsumeRecordVO();
        dst.setId(src.getId());
        dst.setConsumeTime(src.getConsumeTime());
        dst.setAddress(Address.builder().id(src.getId()).build());
        dst.setEaters(JsonUtils.fromJson(src.getEaters(), new TypeToken<List<String>>(){}));
        dst.setComment(src.getComment());
        dst.setTotalCost(src.getTotalCost());
        dst.setComment(src.getComment());
        return dst;
    }

}
