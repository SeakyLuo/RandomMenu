package personalprojects.seakyluo.randommenu.database.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantFoodMapper;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;

public class RestaurantFoodDaoService {

    public static void insert(List<RestaurantFoodVO> voList){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        List<RestaurantFoodDAO> daoList = voList.stream().map(RestaurantFoodDaoService::convert).collect(Collectors.toList());
        List<Long> ids = mapper.insert(daoList);
        for (int i = 0; i < ids.size(); i++){
            voList.get(i).setId(ids.get(i));
        }
    }

    public static void deleteByRestaurant(long restaurantId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        mapper.deleteByRestaurant(restaurantId);
    }

    public static List<RestaurantFoodVO> selectByRestaurantId(long restaurantId){
        RestaurantFoodMapper mapper = AppDatabase.instance.restaurantFoodMapper();
        return mapper.selectByRestaurant(restaurantId).stream()
                .sorted(Comparator.comparing(RestaurantFoodDAO::getOrder))
                .map(RestaurantFoodDaoService::convert)
                .collect(Collectors.toList());
    }

    private static RestaurantFoodDAO convert(RestaurantFoodVO src){
        if (src == null){
            return null;
        }
        RestaurantFoodDAO dst = new RestaurantFoodDAO();
        dst.setId(src.getId());
        dst.setRestaurantId(src.getRestaurantId());
        dst.setConsumeRecordId(src.getConsumeRecordId());
        dst.setPictureUri(src.getPictureUri());
        dst.setName(src.getName());
        dst.setComment(src.getComment());
        dst.setPrice(src.getPrice());
        dst.setShowInList(src.isShowInList());
        return dst;
    }

    private static RestaurantFoodVO convert(RestaurantFoodDAO src){
        if (src == null){
            return null;
        }
        RestaurantFoodVO dst = new RestaurantFoodVO();
        dst.setId(src.getId());
        dst.setConsumeRecordId(src.getConsumeRecordId());
        dst.setPictureUri(src.getPictureUri());
        dst.setName(src.getName());
        dst.setComment(src.getComment());
        dst.setPrice(src.getPrice());
        dst.setShowInList(src.getShowInList());
        return dst;
    }

}
