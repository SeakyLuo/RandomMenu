package personalprojects.seakyluo.randommenu.database.services;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.ConsumeRecordMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantFoodMapper;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.services.ImagePathService;

public class RestaurantFoodDaoService {

    public static void update(RestaurantFoodVO vo){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        mapper.update(convert(vo));
    }

    public static void insert(List<RestaurantFoodVO> voList){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        List<RestaurantFoodDAO> daoList = voList.stream().map(RestaurantFoodDaoService::convert).collect(Collectors.toList());
        List<Long> ids = mapper.insert(daoList);
        for (int i = 0; i < ids.size(); i++){
            Long id = ids.get(i);
            RestaurantFoodVO vo = voList.get(i);
            vo.setId(id);
            ImagePathService.insertRestaurantFood(id, vo.getImages());
        }
    }

    public static void deleteByRestaurant(long restaurantId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        List<RestaurantFoodDAO> foods = mapper.selectByRestaurant(restaurantId);
        mapper.deleteByRestaurant(restaurantId);
        ImagePathService.deleteByRestaurantFoods(foods.stream().map(RestaurantFoodDAO::getId).collect(Collectors.toList()));
    }

    public static void deleteByConsumeRecord(long consumeRecordId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        List<RestaurantFoodDAO> foods = mapper.selectByConsumeRecord(consumeRecordId);
        mapper.deleteByConsumeRecord(consumeRecordId);
        ImagePathService.deleteByRestaurantFoods(foods.stream().map(RestaurantFoodDAO::getId).collect(Collectors.toList()));
    }

    public static List<RestaurantFoodVO> selectByConsumeRecord(long consumeRecordId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        List<RestaurantFoodDAO> foods = mapper.selectByConsumeRecord(consumeRecordId);
        return foods.stream().map(RestaurantFoodDaoService::convert).collect(Collectors.toList());
    }

    public static RestaurantFoodVO selectById(Long id){
        if (id == null){
            return null;
        }
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        RestaurantFoodVO vo = convert(mapper.selectById(id));
        if (vo == null){
            return null;
        }
        vo.setImages(ImagePathService.selectByRestaurantFood(id));
        return vo;
    }

    public static List<RestaurantFoodVO> selectByRestaurantId(long restaurantId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        List<RestaurantFoodVO> foods = mapper.selectByRestaurant(restaurantId).stream()
                .sorted(Comparator.comparing(RestaurantFoodDAO::getOrder))
                .map(RestaurantFoodDaoService::convert)
                .collect(Collectors.toList());
        List<Long> foodIds = foods.stream().map(RestaurantFoodVO::getId).collect(Collectors.toList());
        Map<Long, List<String>> map = ImagePathService.selectByRestaurantFoods(foodIds);
        return foods.stream().peek(f -> f.setImages(map.get(f.getId()))).collect(Collectors.toList());
    }

    public static List<RestaurantFoodVO> search(String keyword){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        return mapper.search(keyword).stream().map(RestaurantFoodDaoService::convert).collect(Collectors.toList());
    }

    public static List<RestaurantFoodVO> selectByRestaurantHome(long restaurantId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        return mapper.selectByRestaurantHome(restaurantId).stream()
                .map(RestaurantFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    public static Map<Long, List<RestaurantFoodVO>> selectByRestaurantsHome(List<Long> restaurantIds){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        return mapper.selectByRestaurantsHome(restaurantIds).stream()
                .collect(Collectors.groupingBy(RestaurantFoodDAO::getRestaurantId))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(Comparator.comparing(RestaurantFoodDAO::getOrderInHome))
                                .map(RestaurantFoodDaoService::convert)
                                .collect(Collectors.toList())));
    }

    private static RestaurantFoodDAO convert(RestaurantFoodVO src){
        if (src == null){
            return null;
        }
        RestaurantFoodDAO dst = new RestaurantFoodDAO();
        dst.setId(src.getId());
        dst.setRestaurantId(src.getRestaurantId());
        dst.setConsumeRecordId(src.getConsumeRecordId());
        dst.setPictureUri(src.getCover());
        dst.setName(src.getName());
        dst.setComment(src.getComment());
        dst.setPrice(src.getPrice());
        dst.setOrderInHome(src.getOrderInHome());
        return dst;
    }

    private static RestaurantFoodVO convert(RestaurantFoodDAO src){
        if (src == null){
            return null;
        }
        RestaurantFoodVO dst = new RestaurantFoodVO();
        dst.setId(src.getId());
        dst.setRestaurantId(src.getRestaurantId());
        dst.setConsumeRecordId(src.getConsumeRecordId());
        dst.setCover(src.getPictureUri());
        dst.setName(src.getName());
        dst.setComment(src.getComment());
        dst.setPrice(src.getPrice());
        dst.setOrderInHome(src.getOrderInHome());
        return dst;
    }

}
