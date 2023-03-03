package personalprojects.seakyluo.randommenu.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import personalprojects.seakyluo.randommenu.database.dao.AddressDAO;
import personalprojects.seakyluo.randommenu.database.dao.ConsumeRecordDAO;
import personalprojects.seakyluo.randommenu.database.dao.FoodTypeDAO;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;
import personalprojects.seakyluo.randommenu.database.mappers.AddressMapper;
import personalprojects.seakyluo.randommenu.database.mappers.ConsumeRecordMapper;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTypeMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantFoodMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantMapper;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;

@Database(entities = {RestaurantDAO.class, AddressDAO.class, ConsumeRecordDAO.class, RestaurantFoodDAO.class, FoodTypeDAO.class},
        version = 6,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "randomMenu.db";
    public static AppDatabase instance;

    public abstract RestaurantMapper restaurantMapper();
    public abstract AddressMapper addressMapper();
    public abstract ConsumeRecordMapper consumeRecordMapper();
    public abstract RestaurantFoodMapper restaurantFoodMapper();
    public abstract FoodTypeMapper foodTypeMapper();

    public static void createInstance(Context context){
        instance = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(new Migration(5, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table restaurant_food add column orderInHome INTEGER DEFAULT -1");
                    }
                })
                .build();
    }
}
