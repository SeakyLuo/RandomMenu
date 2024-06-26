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
import personalprojects.seakyluo.randommenu.database.dao.EaterDAO;
import personalprojects.seakyluo.randommenu.database.dao.FoodTagDAO;
import personalprojects.seakyluo.randommenu.database.dao.FoodTypeDAO;
import personalprojects.seakyluo.randommenu.database.dao.ImagePathDAO;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantDAO;
import personalprojects.seakyluo.randommenu.database.dao.RestaurantFoodDAO;
import personalprojects.seakyluo.randommenu.database.dao.SearchHistoryDAO;
import personalprojects.seakyluo.randommenu.database.dao.SelfMadeFoodDAO;
import personalprojects.seakyluo.randommenu.database.dao.SelfMadeFoodTagDAO;
import personalprojects.seakyluo.randommenu.database.dao.TagMapEntryDAO;
import personalprojects.seakyluo.randommenu.database.mappers.AddressMapper;
import personalprojects.seakyluo.randommenu.database.mappers.ConsumeRecordMapper;
import personalprojects.seakyluo.randommenu.database.mappers.EaterMapper;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTagMapper;
import personalprojects.seakyluo.randommenu.database.mappers.FoodTypeMapper;
import personalprojects.seakyluo.randommenu.database.mappers.ImagePathMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantFoodMapper;
import personalprojects.seakyluo.randommenu.database.mappers.RestaurantMapper;
import personalprojects.seakyluo.randommenu.database.mappers.SearchHistoryMapper;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodMapper;
import personalprojects.seakyluo.randommenu.database.mappers.SelfFoodTagMapper;
import personalprojects.seakyluo.randommenu.database.mappers.TagMapEntryMapper;

@Database(entities = {RestaurantDAO.class, AddressDAO.class, ConsumeRecordDAO.class, RestaurantFoodDAO.class, FoodTypeDAO.class,
        SelfMadeFoodDAO.class, FoodTagDAO.class, SelfMadeFoodTagDAO.class, TagMapEntryDAO.class, ImagePathDAO.class,
        SearchHistoryDAO.class, EaterDAO.class },
        version = 18,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "randomMenu.db";
    public static AppDatabase instance;

    public abstract RestaurantMapper restaurantMapper();
    public abstract AddressMapper addressMapper();
    public abstract ConsumeRecordMapper consumeRecordMapper();
    public abstract RestaurantFoodMapper restaurantFoodMapper();
    public abstract FoodTypeMapper foodTypeMapper();
    public abstract SelfFoodMapper selfFoodMapper();
    public abstract FoodTagMapper foodTagMapper();
    public abstract SelfFoodTagMapper selfFoodTagMapper();
    public abstract TagMapEntryMapper tagMapEntryMapper();
    public abstract ImagePathMapper imagePathMapper();
    public abstract SearchHistoryMapper searchHistoryMapper();
    public abstract EaterMapper eaterMapper();

    public static void createInstance(Context context){
        instance = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
//                .setQueryCallback(new RoomDatabase.QueryCallback() {
//                    @Override
//                    public void onQuery(@NonNull String sqlQuery, @NonNull List<Object> bindArgs) {
//                        for (Object arg:bindArgs){
//                            sqlQuery = sqlQuery.replaceFirst("\\?", arg == null ? "null" : arg.toString());
//                        }
//                        Log.d("RoomDebug", "Executed SQL: " + sqlQuery);
//                    }
//                }, Executors.newSingleThreadExecutor())
                .addMigrations(new Migration(17, 18) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table restaurant add column consumeCount INTEGER NOT NULL DEFAULT 0");
                        database.execSQL("create table eater (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "restaurantId INTEGER NOT NULL," +
                                "consumeRecordId INTEGER NOT NULL," +
                                "eater TEXT" +
                                ")");
                        database.execSQL("CREATE INDEX index_eater_restaurantId on eater (restaurantId)");
                    }
                })
                .addMigrations(new Migration(16, 17) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table restaurant_food add column quantity INTEGER NOT NULL DEFAULT 1");
                    }
                })
                .addMigrations(new Migration(15, 16) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table restaurant add column favorite INTEGER NOT NULL DEFAULT 0");
                    }
                })
                .addMigrations(new Migration(14, 15) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table consume_record add column autoCost INTEGER NOT NULL DEFAULT 0");
                    }
                })
                .addMigrations(new Migration(13, 14) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("create table search_history (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "keyword TEXT," +
                                "searchType TEXT" +
                                ")");
                    }
                })
                .addMigrations(new Migration(12, 13) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                    }
                }, new Migration(11, 12) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("create table image_path (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "itemId INTEGER NOT NULL," +
                                "itemType TEXT," +
                                "path TEXT," +
                                "orderNum INTEGER NOT NULL" +
                                ")");
                        database.execSQL("CREATE INDEX index_image_path on image_path (itemId)");
                    }
                }, new Migration(8, 9) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("drop table self_food");
                        database.execSQL("create table self_food (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "name TEXT," +
                                "note TEXT," +
                                "favorite INTEGER NOT NULL," +
                                "cover INTEGER NOT NULL, " +
                                "foodCover TEXT, " +
                                "dateAdded INTEGER NOT NULL," +
                                "hideCount INTEGER NOT NULL" +
                                ")");
                        database.execSQL("drop table food_tag");
                        database.execSQL("create table food_tag (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "name TEXT," +
                                "foodCount INTEGER NOT NULL" +
                                ")");
                        database.execSQL("drop table self_food_tag");
                        database.execSQL("create table self_food_tag (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "foodId INTEGER NOT NULL," +
                                "tagId INTEGER NOT NULL," +
                                "orderNum INTEGER NOT NULL" +
                                ")");
                        database.execSQL("CREATE INDEX index_self_food_tag_tagId on self_food_tag (tagId)");
                        database.execSQL("CREATE INDEX index_self_food_tag_foodId on self_food_tag (foodId)");
                        database.execSQL("drop table self_food_image");
                        database.execSQL("create table self_food_image (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "foodId INTEGER NOT NULL," +
                                "image TEXT," +
                                "orderNum INTEGER NOT NULL" +
                                ")");
                        database.execSQL("CREATE INDEX index_self_food_image_foodId on self_food_image (foodId)");
                    }
                }, new Migration(9, 10) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("create table auto_tag_map (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "keyword TEXT," +
                                "tags TEXT" +
                                ")");
                    }
                }, new Migration(10, 11) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table self_food add column tags TEXT");
                    }
                }, new Migration(5, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("alter table restaurant_food add column orderInHome INTEGER DEFAULT -1");
                    }
                })
                .build();
    }
}
