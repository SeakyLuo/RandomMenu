package personalprojects.seakyluo.randommenu.database.services;

import com.google.common.collect.Lists;
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
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.services.ImagePathService;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;

public class ConsumeRecordDaoService {

    public static void insert(List<ConsumeRecordVO> voList, long restaurantId, List<AddressVO> addressList){
        if (CollectionUtils.isEmpty(voList)){
            return;
        }
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        List<ConsumeRecordDAO> daoList = voList.stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
        for (int i = 0; i < daoList.size(); i++){
            ConsumeRecordVO vo = voList.get(i);
            ConsumeRecordDAO dao = daoList.get(i);
            dao.setRestaurantId(restaurantId);
            AddressVO address = vo.getAddress();
            dao.setAddressId(addressList.stream().filter(a -> a.equals(address)).map(AddressVO::getId).findFirst().get());
        }
        List<Long> ids = mapper.insert(daoList);
        setRestaurantIdAndRecordId(voList, restaurantId, ids);
        for (ConsumeRecordVO vo : voList){
            ImagePathService.insertConsumeRecord(vo.getId(), vo.getEnvironmentPictures());
        }
        computeShowFood(voList);
        List<RestaurantFoodVO> foods = voList.stream().flatMap(vo -> vo.getFoods().stream().peek(f -> f.setId(0))).collect(Collectors.toList());
        RestaurantFoodDaoService.insert(foods);
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
     * 点的最多的+有图片+有评论+评论有好吃的+最后消费的
     * */
    private static void computeShowFood(List<ConsumeRecordVO> records){
        Map<Long, ConsumeRecordVO> recordMap = records.stream().collect(Collectors.toMap(ConsumeRecordVO::getId, Function.identity()));
        List<RestaurantFoodVO> foods = records.stream().flatMap(r -> r.getFoods().stream()).collect(Collectors.toList());
        Map<String, Integer> counter = new HashMap<>();
        for (RestaurantFoodVO food : foods){
            String name = food.getName();
            if (StringUtils.isEmpty(name)){
                continue;
            }
            counter.put(name, counter.getOrDefault(name, 0) + 1);
        }
        foods.sort(Comparator.comparing(f -> computeSortPoints((RestaurantFoodVO) f, counter))
                .thenComparing(f -> -recordMap.get(((RestaurantFoodVO) f).getConsumeRecordId()).getConsumeTime())
                .thenComparing(f -> recordMap.get(((RestaurantFoodVO) f).getConsumeRecordId()).getFoods().indexOf(f)));
        int showCounter = 0;
        for (int i = 0; i < foods.size(); i++){
            RestaurantFoodVO curr = foods.get(i);
            // 避免重名菜
            if (i == 0 || (showCounter < MAX_FOOD_SHOW && foods.stream().limit(i).noneMatch(f -> StringUtils.equals(f.getName(), curr.getName())))){
                curr.setOrderInHome(showCounter++);
            } else {
                curr.setOrderInHome(-1);
            }
        }
    }

    private static int computeSortPoints(RestaurantFoodVO food, Map<String, Integer> counter){
        int points = 100;
        String comment = food.getComment();
        if (isPositiveComment(comment)){
            points += 30;
        }
        else if (isNotBadComment(comment)){
            points += 9;
        }
        else if (StringUtils.isNotEmpty(comment)){
            points += 3;
        }
        points += (counter.getOrDefault(food.getName(), 0) - 1) * 10;
        if (StringUtils.isNotEmpty(food.getCover())){
            points += 6;
        }
        return -points;
    }

    private static boolean isPositiveComment(String comment){
        if (StringUtils.isEmpty(comment)){
            return false;
        }
        return comment.matches("^(?!不).*(好|爱|喜欢|推荐|棒|赞).*") || comment.contains("必点") || comment.contains("还不错") || comment.contains("yyds");
    }

    private static boolean isNotBadComment(String comment){
        if (StringUtils.isEmpty(comment)){
            return false;
        }
        return comment.contains("还行") || comment.contains("还可以");
    }

    public static void update(List<ConsumeRecordVO> voList, long restaurantId, List<AddressVO> addressList){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        mapper.deleteByRestaurant(restaurantId);
        RestaurantFoodDaoService.deleteByRestaurant(restaurantId);
        List<Long> ids = voList.stream().map(ConsumeRecordVO::getId).collect(Collectors.toList());
        ImagePathService.deleteByConsumeRecords(ids);
        insert(voList, restaurantId, addressList);
    }

    public static void insert(ConsumeRecordVO vo){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        ConsumeRecordDAO dao = convert(vo);
        dao.setAddressId(vo.getAddress().getId());
        Long id = mapper.insert(dao);
        RestaurantFoodDaoService.insert(vo.getFoods());
        ImagePathService.insertConsumeRecord(id, vo.getEnvironmentPictures());
    }

    public static void update(ConsumeRecordVO vo){
        delete(vo);
        insert(vo);
    }

    public static void delete(ConsumeRecordVO vo){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        mapper.delete(convert(vo));
        RestaurantFoodDaoService.deleteByConsumeRecord(vo.getId());
        ImagePathService.deleteByConsumeRecords(Lists.newArrayList(vo.getId()));
    }

    public static List<ConsumeRecordVO> selectByRestaurant(long restaurantId){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        List<ConsumeRecordVO> records = mapper.selectByRestaurant(restaurantId).stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(records)){
            return records;
        }
        Map<Long, List<RestaurantFoodVO>> foodMap = RestaurantFoodDaoService.selectByRestaurantId(restaurantId).stream().collect(Collectors.groupingBy(RestaurantFoodVO::getConsumeRecordId));
        Map<Long, AddressVO> addressMap = AddressDaoService.selectByRestaurant(restaurantId).stream().collect(Collectors.toMap(AddressVO::getId, Function.identity()));
        List<Long> ids = records.stream().map(ConsumeRecordVO::getId).collect(Collectors.toList());
        Map<Long, List<String>> imageMap = ImagePathService.selectByConsumeRecords(ids);
        for (ConsumeRecordVO record : records){
            long recordId = record.getId();
            record.setAddress(addressMap.getOrDefault(record.getAddress().getId(), record.getAddress()));
            record.setFoods(foodMap.get(recordId));
            record.setEnvironmentPictures(imageMap.get(recordId));
        }
        return records;
    }

    public static List<ConsumeRecordVO> search(String keyword){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        return mapper.search(keyword).stream().map(ConsumeRecordDaoService::convert).collect(Collectors.toList());
    }

    public static List<String> selectAllEaters(){
        ConsumeRecordMapper mapper = AppDatabase.instance.consumeRecordMapper();
        return mapper.selectAllEaters().stream()
                .filter(StringUtils::isNotEmpty)
                .flatMap(i -> JsonUtils.fromJson(i, new TypeToken<List<String>>(){}).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private static ConsumeRecordDAO convert(ConsumeRecordVO src){
        if (src == null){
            return null;
        }
        ConsumeRecordDAO dst = new ConsumeRecordDAO();
        dst.setId(src.getId());
        dst.setRestaurantId(src.getRestaurantId());
        dst.setConsumeTime(src.getConsumeTime());
        dst.setAddressId(src.getAddress().getId());
        dst.setEaters(JsonUtils.toJson(src.getEaters()));
        dst.setComment(src.getComment());
        dst.setTotalCost(src.getTotalCost());
        dst.setComment(src.getComment());
        dst.setAutoCost(src.isAutoCost());
        return dst;
    }

    private static ConsumeRecordVO convert(ConsumeRecordDAO src){
        if (src == null){
            return null;
        }
        ConsumeRecordVO dst = new ConsumeRecordVO();
        dst.setId(src.getId());
        dst.setRestaurantId(src.getRestaurantId());
        dst.setConsumeTime(src.getConsumeTime());
        dst.setAddress(AddressVO.builder().id(src.getAddressId()).build());
        dst.setEaters(JsonUtils.fromJson(src.getEaters(), new TypeToken<List<String>>(){}));
        dst.setComment(src.getComment());
        dst.setTotalCost(src.getTotalCost());
        dst.setComment(src.getComment());
        dst.setAutoCost(src.isAutoCost());
        return dst;
    }

}
