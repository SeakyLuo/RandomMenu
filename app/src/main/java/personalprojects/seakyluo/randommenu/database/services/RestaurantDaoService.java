package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantMapper;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantDaoService {

    public static void save(RestaurantVO vo){
        if (vo.getId() == 0){
            insert(vo);
        } else {
            update(vo);
        }
    }

    public static void insert(RestaurantVO vo){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        RestaurantDAO dao = convert(vo);
        mapper.insert(dao);
        int id = dao.getId();
        vo.setId(id);
        AddressDaoService.insert(vo.getAddressList(), id);
        ConsumeRecordDaoService.insert(vo.getRecords(), id, vo.getAddressList());
    }

    public static void update(RestaurantVO vo){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        mapper.update(convert(vo));
    }

    public static List<RestaurantVO> selectByPage(int pageNum, int pageSize){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        List<RestaurantDAO> daoList = mapper.selectByPage(pageNum, pageSize);
        List<RestaurantVO> voList = daoList.stream().map(RestaurantDaoService::convert).collect(Collectors.toList());
        for (RestaurantVO vo : voList){
            int restaurantId = vo.getId();
            vo.setAddressList(AddressDaoService.selectByRestaurant(restaurantId));
            vo.setRecords(ConsumeRecordDaoService.selectByRestaurant(restaurantId));
        }
        return voList;
    }

    private static RestaurantVO convert(RestaurantDAO src){
        if (src == null){
            return null;
        }
        RestaurantVO dst = new RestaurantVO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        String foodTypeCode = src.getFoodTypeCode();
        dst.setFoodTypeCode(foodTypeCode);
        dst.setFoodTypeName(FoodType.getNameByCode(foodTypeCode));
        dst.setComment(src.getComment());
        dst.setLink(src.getLink());
        return dst;
    }

    private static RestaurantDAO convert(RestaurantVO src){
        if (src == null){
            return null;
        }
        RestaurantDAO dst = new RestaurantDAO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setFoodTypeCode(src.getFoodTypeCode());
        dst.setComment(src.getComment());
        dst.setLink(src.getLink());
        return dst;
    }

}
