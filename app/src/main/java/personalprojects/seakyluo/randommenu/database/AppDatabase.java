package personalprojects.seakyluo.randommenu.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;
import personalprojects.seakyluo.randommenu.database.dao.ConsumeRecordDAO;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.AddressMapper;
import personalprojects.seakyluo.randommenu.database.mappers.ConsumeRecordMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantFoodMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantMapper;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;

@Database(entities = {RestaurantDAO.class, AddressDAO.class, ConsumeRecordDAO.class, RestaurantFoodDAO.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase instance;

    public abstract RestaurantMapper restaurantMapper();
    public abstract AddressMapper addressMapper();
    public abstract ConsumeRecordMapper consumeRecordMapper();
    public abstract RestaurantFoodMapper restaurantFoodMapper();

    public static void createInstance(Context context){
        instance = Room.databaseBuilder(context, AppDatabase.class, "randomMenu.db")
                .allowMainThreadQueries()
                .build();
    }
}
