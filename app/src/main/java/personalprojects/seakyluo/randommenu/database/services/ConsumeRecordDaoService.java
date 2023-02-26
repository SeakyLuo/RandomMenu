package personalprojects.seakyluo.randommenu.database.services;

import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections.CollectionUtils;

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

    public static void insert(List<ConsumeRecordVO> voList, long restaurantId, List<Address> addressList){
        if (CollectionUtils.isEmpty(voList)){
            return;
        }
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        List<ConsumeRecordDAO> daoList = voList.stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
        for (int i = 0; i < daoList.size(); i++){
            ConsumeRecordVO vo = voList.get(i);
            ConsumeRecordDAO dao = daoList.get(i);
            dao.setRestaurantId(restaurantId);
            dao.setAddressId(addressList.stream().filter(a -> a.equals(vo.getAddress())).findFirst().map(Address::getId).get());
            dao.setOrder(i);
        }
        List<Long> ids = mapper.insert(daoList);
        for (int i = 0; i < daoList.size(); i++){
            ConsumeRecordVO vo = voList.get(i);
            long id = ids.get(i);
            vo.setId(id);
            RestaurantFoodDaoService.insert(vo.getFoods(), restaurantId, id);
        }
    }

    public static void update(List<ConsumeRecordVO> voList, long restaurantId, List<Address> addressList){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        mapper.deleteByRestaurant(restaurantId);
        RestaurantFoodDaoService.deleteByRestaurant(restaurantId);
        insert(voList, restaurantId, addressList);
    }

    public static void update(ConsumeRecordVO vo, List<Address> addressList){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        ConsumeRecordDAO dao = convert(vo);
        dao.setAddressId(addressList.stream().filter(a -> a.equals(vo.getAddress())).findFirst().map(Address::getId).get());
        mapper.update(dao);
    }

    public static List<ConsumeRecordVO> selectByRestaurant(long restaurantId){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        List<ConsumeRecordVO> records = mapper.selectByRestaurant(restaurantId).stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(records)){
            return records;
        }
        Map<Long, List<RestaurantFoodVO>> foodMap = RestaurantFoodDaoService.selectByRestaurantId(restaurantId).stream().collect(Collectors.groupingBy(RestaurantFoodVO::getConsumeRecordId));
        Map<Long, Address> addressMap = AddressDaoService.selectByRestaurant(restaurantId).stream().collect(Collectors.toMap(Address::getId, Function.identity()));
        for (ConsumeRecordVO record : records){
            record.setAddress(addressMap.getOrDefault(record.getAddress().getId(), record.getAddress()));
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
        dst.setAddress(Address.builder().id(src.getAddressId()).build());
        dst.setEaters(JsonUtils.fromJson(src.getEaters(), new TypeToken<List<String>>(){}));
        dst.setComment(src.getComment());
        dst.setTotalCost(src.getTotalCost());
        dst.setComment(src.getComment());
        return dst;
    }

}
