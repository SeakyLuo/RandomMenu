package personalprojects.seakyluo.randommenu.database.services;

import androidx.sqlite.db.SimpleSQLiteQuery;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.Page;
import personalprojects.seakyluo.randommenu.database.dao.PagedData;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantMapper;
import personalprojects.seakyluo.randommenu.enums.RestaurantOrderByField;
import personalprojects.seakyluo.randommenu.models.RestaurantFilter;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.services.FoodTypeService;

public class RestaurantDaoService {

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
                insert(mapper, vo);
            }
        });
    }

    public static void insert(RestaurantVO vo){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        AppDatabase.instance.runInTransaction(() -> {
            insert(mapper, vo);
        });
    }

    private static void insert(RestaurantMapper mapper, RestaurantVO vo){
        FoodTypeService.save(vo.getFoodType());
        RestaurantDAO dao = convert(vo);
        long id = mapper.insert(dao);
        vo.setId(id);
        List<AddressVO> addressList = vo.getAddressList();
        AddressDaoService.insert(addressList, id);
        ConsumeRecordDaoService.insert(vo.getRecords(), id, addressList);
    }

    public static void setRestaurantFavorite(RestaurantVO vo, boolean favorite){
        vo.setFavorite(favorite);
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        RestaurantDAO dao = convert(vo);
        dao.setFavorite(favorite);
        mapper.update(dao);
    }

    public static long countFavorites(){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        return mapper.countFavorites(true);
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
    }

    public static void delete(RestaurantVO vo){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        mapper.delete(convert(vo));
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

    public static PagedData<RestaurantVO> selectByPage(int pageNum, int pageSize, RestaurantFilter filter){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        long count;
        List<RestaurantDAO> daoList;
        if (filter == null || filter.isEmpty()){
            daoList = mapper.selectByPage(pageNum, pageSize);
            count = mapper.selectCount();
        } else {
            List<Long> ids = mapper.filter(buildPagedFilterSQL(pageNum, pageSize, filter));
            Map<Long, RestaurantDAO> daoMap = mapper.selectByIds(ids).stream().collect(Collectors.toMap(RestaurantDAO::getId, Function.identity()));
            daoList = ids.stream().map(daoMap::get).filter(Objects::nonNull).collect(Collectors.toList());
            count = mapper.selectCountByFilter(buildCountFilterSQL(filter));
        }
        List<RestaurantVO> voList = daoList.stream().map(RestaurantDaoService::convert).collect(Collectors.toList());
        voList.parallelStream().forEach(vo -> {
            long restaurantId = vo.getId();
            vo.setAddressList(AddressDaoService.selectByRestaurant(restaurantId));
            vo.setFoods(RestaurantFoodDaoService.selectByRestaurantHome(restaurantId));
        });
        PagedData<RestaurantVO> data = new PagedData<>();
        data.setPage(new Page(pageNum, pageSize, count));
        data.setData(voList);
        return data;
    }

    public static List<RestaurantVO> selectFavorites(){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        List<RestaurantDAO> daoList = mapper.selectFavorites(true);
        List<RestaurantVO> voList = daoList.stream().map(RestaurantDaoService::convert).collect(Collectors.toList());
        voList.parallelStream().forEach(vo -> {
            long restaurantId = vo.getId();
            vo.setAddressList(AddressDaoService.selectByRestaurant(restaurantId));
            vo.setFoods(RestaurantFoodDaoService.selectByRestaurantHome(restaurantId));
        });
        return voList;
    }

    private static SimpleSQLiteQuery buildPagedFilterSQL(int pageNum, int pageSize, RestaurantFilter filter){
        List<RestaurantOrderByField> orderByDesc = filter.getOrderByDesc();
        String orderByDescSQL = "restaurant.lastVisitTime";
        if (CollectionUtils.isNotEmpty(orderByDesc)){
            orderByDescSQL = orderByDesc.stream().map(f -> "restaurant." + f.name()).collect(Collectors.joining(","));
        }
        String baseSQL = "SELECT distinct restaurant.id FROM restaurant " + buildFilterSQL(filter)
                + " order by " + orderByDescSQL + " desc limit " + pageSize + " offset " + ((pageNum - 1) * pageSize);
        return new SimpleSQLiteQuery(baseSQL);
    }

    private static SimpleSQLiteQuery buildCountFilterSQL(RestaurantFilter filter){
        return new SimpleSQLiteQuery("SELECT count(distinct restaurant.id) FROM restaurant " + buildFilterSQL(filter));
    }

    private static String buildFilterSQL(RestaurantFilter filter){
        String s = "";
        Long startTime = filter.getStartTime();
        Long endTime = filter.getEndTime();
        List<String> eaterList = filter.getEaters();
        FoodType foodType = filter.getFoodType();
        if (startTime != null || endTime != null || eaterList != null || filter.isEatAlone()){
            s += "join consume_record cr on restaurant.id = cr.restaurantId ";
            if (startTime != null){
                s += "and cr.consumeTime >= " + startTime + " ";
            }
            if (endTime != null){
                s += "and cr.consumeTime <= " + endTime + " ";
            }
            if (filter.isEatAlone()){
                s += "and cr.eaters = '[]' ";
            }
            else if (CollectionUtils.isNotEmpty(eaterList)){
                StringBuilder builder = new StringBuilder();
                for (String eater : eaterList){
                    builder.append("and cr.eaters like '%").append(eater).append("%' ");
                }
                s += builder.toString();
            }
        }
        AddressVO address = filter.getAddress();
        if (address != null){
            s += "join ADDRESS address on restaurant.id = address.restaurantId ";
            String province = address.getProvince();
            String city = address.getCity();
            String county = address.getCounty();
            if (province != null){
                s += "and address.province = '" + province + "' ";
            }
            if (city != null){
                s += "and address.city = '" + city + "' ";
            }
            if (county != null){
                s += "and address.county = '" + county + "' ";
            }
        }
        s += "where 1 ";
        if (foodType != null){
            s += "and restaurant.foodTypeId = " + foodType.getId() + " ";
        }
        return s;
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

    public static List<RestaurantVO> search(String keyword){
        RestaurantMapper mapper = AppDatabase.instance.restaurantMapper();
        List<RestaurantVO> restaurants = mapper.search(keyword).stream()
                .map(RestaurantDaoService::convert)
                .collect(Collectors.toList());
        Set<Long> ids = restaurants.stream().map(RestaurantVO::getId).collect(Collectors.toSet());
        List<Long> consumeRecordRestaurantIds = ConsumeRecordDaoService.search(keyword).stream().map(ConsumeRecordVO::getRestaurantId).distinct().filter(i -> !ids.contains(i)).collect(Collectors.toList());
        restaurants.addAll(mapper.selectByIds(consumeRecordRestaurantIds).stream().map(RestaurantDaoService::convert).collect(Collectors.toList()));
        ids.addAll(consumeRecordRestaurantIds);
        List<RestaurantFoodVO> foods = RestaurantFoodDaoService.search(keyword);
        List<Long> foodRestaurantIds = foods.stream().map(RestaurantFoodVO::getRestaurantId).filter(i -> !ids.contains(i)).collect(Collectors.toList());
        Map<Long, List<RestaurantFoodVO>> foodMap = foods.stream().collect(Collectors.groupingBy(RestaurantFoodVO::getRestaurantId));
        List<RestaurantVO> foodRestaurants = mapper.selectByIds(foodRestaurantIds).stream()
                .map(RestaurantDaoService::convert)
                .peek(i -> i.setFoods(foodMap.get(i.getId())))
                .collect(Collectors.toList());
        restaurants.addAll(foodRestaurants);
        ids.addAll(foodRestaurantIds);

        Map<Long, List<AddressVO>> addressMap = AddressDaoService.selectByRestaurants(ids);
        restaurants.parallelStream().forEach(restaurant -> {
            restaurant.setAddressList(addressMap.get(restaurant.getId()));
            if (restaurant.getFoods() == null){
                restaurant.setFoods(RestaurantFoodDaoService.selectByRestaurantHome(restaurant.getId()));
            }
        });
        return restaurants;
    }

    private static RestaurantVO convert(RestaurantDAO src){
        if (src == null){
            return null;
        }
        RestaurantVO dst = new RestaurantVO();
        dst.setId(src.getId());
        dst.setName(src.getName());
        long foodTypeId = src.getFoodTypeId();
        if (foodTypeId != 0){
            dst.setFoodType(new FoodType(foodTypeId, FoodTypeService.getNameById(foodTypeId)));
        }
        dst.setComment(src.getComment());
        dst.setLink(src.getLink());
        dst.setAverageCost(src.getAverageCost());
        dst.setFavorite(src.isFavorite());
        dst.setConsumeCount(src.getConsumeCount());
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
        dst.setFavorite(src.isFavorite());
        List<ConsumeRecordVO> records = src.getRecords();
        dst.setConsumeCount(records.size());
        dst.setFirstVisitTime(records.stream().min(Comparator.comparing(ConsumeRecordVO::getConsumeTime)).map(ConsumeRecordVO::getConsumeTime).orElse(System.currentTimeMillis()));
        dst.setLastVisitTime(records.stream().max(Comparator.comparing(ConsumeRecordVO::getConsumeTime)).map(ConsumeRecordVO::getConsumeTime).orElse(System.currentTimeMillis()));
        return dst;
    }

}
