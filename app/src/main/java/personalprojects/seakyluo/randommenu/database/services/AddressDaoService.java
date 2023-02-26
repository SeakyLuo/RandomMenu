package personalprojects.seakyluo.randommenu.database.services;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.MainActivity;
import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;
import personalprojects.seakyluo.randommenu.database.mappers.AddressMapper;
import personalprojects.seakyluo.randommenu.models.Address;

public class AddressDaoService {

    public static void insert(List<Address> addressList, int restaurantId){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        List<AddressDAO> daoList = addressList.stream().map(AddressDaoService::convert).collect(Collectors.toList());
        for (int i = 0; i < daoList.size(); i++){
            AddressDAO dao = daoList.get(i);
            dao.setRestaurantId(restaurantId);
            dao.setOrder(i);
        }
        mapper.insert(daoList);
        for (int i = 0; i < daoList.size(); i++){
            addressList.get(i).setId(daoList.get(i).getId());
        }
    }

    public static List<Address> selectByRestaurant(int restaurantId){
        AddressMapper mapper = AppDatabase.instance.addressMapper();
        return mapper.selectByRestaurant(restaurantId).stream().map(AddressDaoService::convert).collect(Collectors.toList());
    }

    private static Address convert(AddressDAO src){
        if (src == null){
            return null;
        }
        Address dst = new Address();
        dst.setProvince(src.getProvince());
        dst.setCity(src.getCity());
        dst.setCounty(src.getCounty());
        dst.setAddress(src.getAddress());
        return dst;
    }

    private static AddressDAO convert(Address src){
        if (src == null){
            return null;
        }
        AddressDAO dst = new AddressDAO();
        dst.setProvince(src.getProvince());
        dst.setCity(src.getCity());
        dst.setCounty(src.getCounty());
        dst.setAddress(src.getAddress());
        return dst;
    }
}
