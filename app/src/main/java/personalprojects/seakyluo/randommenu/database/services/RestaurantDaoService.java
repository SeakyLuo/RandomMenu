package personalprojects.seakyluo.randommenu.database.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantMapper;
import personalprojects.seakyluo.randommenu.interfaces.RestaurantListener;
import personalprojects.seakyluo.randommenu.models.AddressVO;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.services.FoodTypeService;

public class RestaurantDaoService {

    private static List<RestaurantListener> listeners = new ArrayList<>();
    public static void addListener(RestaurantListener listener){
        listeners.add(listener);
    }

    public static void save(RestaurantVO vo){
        if (vo.getId() == 0){
            insert(vo);
        } else {
            update(vo);
        }
    }

    public static void insert(List<RestaurantVO> restaurants){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        AppDatabase.instance.runInTransaction(() -> {
            for (RestaurantVO vo : restaurants){
                insert(vo);
            }
        });
    }

    public static void insert(RestaurantVO vo){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        AppDatabase.instance.runInTransaction(() -> {
            FoodTypeService.save(vo.getFoodType());
            RestaurantDAO dao = convert(vo);
            long id = mapper.insert(dao);
            vo.setId(id);
            List<AddressVO> addressList = vo.getAddressList();
            AddressDaoService.insert(addressList, id);
            ConsumeRecordDaoService.insert(vo.getRecords(), id, addressList);
        });
    }

    public static void update(RestaurantVO vo){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        long id = vo.getId();
        RestaurantVO existed = selectById(id);
        if (existed == null){
            insert(vo);
            return;
        }
        AppDatabase.instance.runInTransaction(() -> {
            FoodTypeService.save(vo.getFoodType());
            FoodType existedFoodType = existed.getFoodType();
            if (existedFoodType != null && mapper.countByFoodType(existedFoodType.getId()) == 0){
                FoodTypeService.delete(existedFoodType);
            }

            mapper.update(convert(vo));
            List<AddressVO> addressList = vo.getAddressList();
            AddressDaoService.update(addressList, id);
            ConsumeRecordDaoService.update(vo.getRecords(), id, addressList);
        });
        listeners.forEach(l -> l.onUpdate(vo));
    }

    public static RestaurantVO selectById(long id){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        RestaurantDAO dao = mapper.selectById(id);
        if (dao == null){
            return null;
        }
        RestaurantVO vo = convert(dao);
        vo.setAddressList(AddressDaoService.selectByRestaurant(id));
        vo.setRecords(ConsumeRecordDaoService.selectByRestaurant(id));
        return vo;
    }

    public static List<RestaurantVO> selectByPage(int pageNum, int pageSize){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        List<RestaurantDAO> daoList = mapper.selectByPage(pageNum, pageSize);
        List<RestaurantVO> voList = daoList.stream().map(RestaurantDaoService::convert).collect(Collectors.toList());
        for (RestaurantVO vo : voList){
            long restaurantId = vo.getId();
            vo.setAddressList(AddressDaoService.selectByRestaurant(restaurantId));
            vo.setFoods(RestaurantFoodDaoService.selectByRestaurantHome(restaurantId));
        }
        return voList;
    }

    public static List<RestaurantVO> selectAll(){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        List<RestaurantVO> voList = new ArrayList<>();
        int currentPage = 1, pageSize = 20;
        while (true){
            List<RestaurantDAO> daoList = mapper.selectByPage(currentPage++, pageSize);
            voList.addAll(daoList.stream().map(dao -> selectById(dao.getId())).collect(Collectors.toList()));
            if (daoList.size() < pageSize){
                break;
            }
        }
        return voList;
    }

    public static RestaurantVO selectPagedView(long id){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        RestaurantDAO dao = mapper.selectById(id);
        if (dao == null){
            return null;
        }
        RestaurantVO vo = convert(dao);
        long restaurantId = vo.getId();
        vo.setAddressList(AddressDaoService.selectByRestaurant(restaurantId));
        vo.setFoods(RestaurantFoodDaoService.selectByRestaurantHome(restaurantId));
        return vo;
    }

    private static RestaurantVO convert(RestaurantDAO src){
        if (src == null){
            return null;
        }
        RestaurantVO dst = new RestaurantVO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        long foodTypeId = src.getFoodTypeId();
        dst.setFoodType(new FoodType(foodTypeId, FoodTypeService.getNameById(foodTypeId)));
        dst.setComment(src.getComment());
        dst.setLink(src.getLink());
        dst.setAverageCost(src.getAverageCost());
        return dst;
    }

    private static RestaurantDAO convert(RestaurantVO src){
        if (src == null){
            return null;
        }
        RestaurantDAO dst = new RestaurantDAO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setFoodTypeId(src.getFoodType().getId());
        dst.setComment(src.getComment());
        dst.setLink(src.getLink());
        double averageCost = src.computeAverageCost();
        src.setAverageCost(averageCost);
        dst.setAverageCost(averageCost);
        List<ConsumeRecordVO> records = src.getRecords();
        dst.setFirstVisitTime(records.stream().min(Comparator.comparing(ConsumeRecordVO::getConsumeTime)).map(ConsumeRecordVO::getConsumeTime).orElse(System.currentTimeMillis()));
        dst.setLastVisitTime(records.stream().max(Comparator.comparing(ConsumeRecordVO::getConsumeTime)).map(ConsumeRecordVO::getConsumeTime).orElse(System.currentTimeMillis()));
        return dst;
    }

}
