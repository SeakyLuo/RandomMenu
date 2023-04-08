package personalprojects.seakyluo.randommenu.database.services;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;
import personalprojects.seakyluo.randommenu.database.mappers.AddressMapper;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;

public class AddressDaoService {

    public static void insert(List<AddressVO> addressList, long restaurantId){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        List<AddressDAO> daoList = convert(addressList, restaurantId).stream()
                .peek(i -> i.setId(0))
                .collect(Collectors.toList());
        List<Long> ids = mapper.insert(daoList);
        for (int i = 0; i < daoList.size(); i++){
            addressList.get(i).setId(ids.get(i));
            daoList.get(i).setId(ids.get(i));
        }
    }

    public static void update(List<AddressVO> addressList, long restaurantId){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        mapper.deleteByRestaurant(restaurantId);
        insert(addressList, restaurantId);
    }

    public static void update(AddressVO address){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        mapper.update(convert(address));
    }

    public static List<AddressVO> selectByRestaurant(long restaurantId){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        return mapper.selectByRestaurant(restaurantId).stream().map(AddressDaoService::convert).collect(Collectors.toList());
    }

    public static Map<Long, List<AddressVO>> selectByRestaurants(Collection<Long> restaurantIds){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        return mapper.selectByRestaurants(restaurantIds).stream()
                .collect(Collectors.groupingBy(AddressDAO::getRestaurantId))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(Comparator.comparing(AddressDAO::getOrder))
                                .map(AddressDaoService::convert)
                                .collect(Collectors.toList())));
    }

    private static List<AddressDAO> convert(List<AddressVO> addressList, long restaurantId){
        List<AddressDAO> daoList = addressList.stream().map(AddressDaoService::convert).collect(Collectors.toList());
        for (int i = 0; i < daoList.size(); i++){
            AddressDAO dao = daoList.get(i);
            dao.setRestaurantId(restaurantId);
            dao.setOrder(i);
        }
        return daoList;
    }

    private static AddressVO convert(AddressDAO src){
        if (src == null){
            return null;
        }
        AddressVO dst = new AddressVO();
        dst.setId(src.getId());
        dst.setProvince(src.getProvince());
        dst.setCity(src.getCity());
        dst.setCounty(src.getCounty());
        dst.setAddress(src.getAddress());
        return dst;
    }

    private static AddressDAO convert(AddressVO src){
        if (src == null){
            return null;
        }
        AddressDAO dst = new AddressDAO();
        dst.setId(src.getId());
        dst.setProvince(src.getProvince());
        dst.setCity(src.getCity());
        dst.setCounty(src.getCounty());
        dst.setAddress(src.getAddress());
        return dst;
    }
}
