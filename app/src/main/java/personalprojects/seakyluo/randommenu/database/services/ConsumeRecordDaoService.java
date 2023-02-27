package personalprojects.seakyluo.randommenu.database.services;

import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
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
        }
        List<Long> ids = mapper.insert(daoList);
        setRestaurantIdAndRecordId(voList, restaurantId, ids);
        computeShowFood(voList);
        RestaurantFoodDaoService.insert(voList.stream().flatMap(vo -> vo.getFoods().stream()).collect(Collectors.toList()));
    }

    private static void setRestaurantIdAndRecordId(List<ConsumeRecordVO> voList, long restaurantId, List<Long> ids){
        for (int i = 0; i < voList.size(); i++){
            ConsumeRecordVO vo = voList.get(i);
            long id = ids.get(i);
            vo.setId(id);
            for (RestaurantFoodVO food : vo.getFoods()){
                food.setRestaurantId(restaurantId);
                food.setConsumeRecordId(id);
            }
        }
    }

    private static final int MAX_FOOD_SHOW = 5;
    /**
     * 点的最多的+有图片+有评论+最后消费的
     * */
    private static void computeShowFood(List<ConsumeRecordVO> records){
        Map<Long, ConsumeRecordVO> recordMap = records.stream().collect(Collectors.toMap(ConsumeRecordVO::getId, Function.identity()));
        List<RestaurantFoodVO> foods = records.stream().flatMap(r -> r.getFoods().stream()).collect(Collectors.toList());
        Map<String, Integer> counter = new HashMap<>();
        for (RestaurantFoodVO food : foods){
            String name = food.getName();
            counter.put(name, counter.getOrDefault(name, 0) + 1);
        }
        foods.sort((f1, f2) -> {
            int diff = counter.get(f2.getName()) - counter.get(f1.getName());
            if (diff != 0) return diff;
            diff = Boolean.compare(StringUtils.isNotEmpty(f2.getPictureUri()), StringUtils.isNotEmpty(f1.getPictureUri()));
            if (diff != 0) return diff;
            diff = Boolean.compare(StringUtils.isNotEmpty(f2.getComment()), StringUtils.isNotEmpty(f1.getComment()));
            if (diff != 0) return diff;
            ConsumeRecordVO r1 = recordMap.get(f1.getConsumeRecordId());
            ConsumeRecordVO r2 = recordMap.get(f2.getConsumeRecordId());
            diff = Long.compare(r2.getConsumeTime(), r1.getConsumeTime());
            if (diff != 0) return diff;
            return r2.getFoods().indexOf(f1) - r1.getFoods().indexOf(f2);
        });
        int showCounter = 0;
        for (int i = 0; i < foods.size(); i++){
            RestaurantFoodVO curr = foods.get(i);
            // 避免重名菜
            if (i == 0 || (showCounter < MAX_FOOD_SHOW && !foods.get(i - 1).getName().equals(curr.getName()))){
                curr.setShowInList(true);
                showCounter++;
            } else {
                curr.setShowInList(false);
            }
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
