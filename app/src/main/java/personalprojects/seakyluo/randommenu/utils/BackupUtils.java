package personalprojects.seakyluo.randommenu.utils;

import android.app.Activity;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;

public class BackupUtils {
    private static final String TEMP_BACKUP_NAME = "BackupData";
    private static final String RESTAURANT_FILENAME = "restaurants.json",
            SELF_FOOD_FILENAME = "selfFoods.json",
            AUTO_TAG_MAP_FILENAME = "autoTagMap.json";

    public static void Log(String content){
        FileUtils.writeFile("Logs/" + now() + ".txt" , content);
    }

    public static void init(Activity activity){
        ImageUtils.init(activity);
        FileUtils.init(activity);
        AppDatabase.createInstance(activity);
        String settings = FileUtils.readFile(activity, Settings.FILENAME);
        Settings.settings = Settings.fromJson(settings);
        script(Settings.settings);
        if (StringUtils.isEmpty(settings)) Log("Empty Settings Created");
    }

    private static void script(Settings settings){
    }

    public static String now() { return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd_HHmmss"); }

    public static void save(){
        String settings = Settings.settings.toString(), emptyJson = new Settings().toString();
        if (settings.equals(emptyJson) && !FileUtils.readFile(Settings.FILENAME).equals(emptyJson)){
            Log("Trying to overwrite Settings with Empty content.\nStackTrace:" + Arrays.toString(Thread.currentThread().getStackTrace()));
            return;
        }
        FileUtils.writeFile(Settings.FILENAME, settings);
        try {
            FileUtils.zip(FileUtils.TEMP_FOLDER, TEMP_BACKUP_NAME + now() + ".zip", getBackupDataFiles());
        } catch (Exception ignored){

        }
        File[] tempFiles = FileUtils.TEMP_FOLDER.listFiles();
        if (tempFiles == null){
            return;
        }
        // 保留最新10个备份
        Arrays.stream(tempFiles).filter(f -> f.getName().startsWith(TEMP_BACKUP_NAME))
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .skip(10)
                .forEach(File::delete);
    }

    public static String buildRestaurantFilename(){
        return FileUtils.TEMP_FOLDER.getName() + File.separator + RESTAURANT_FILENAME;
    }

    public static String buildSelfFoodFilename(){
        return FileUtils.TEMP_FOLDER.getName() + File.separator + SELF_FOOD_FILENAME;
    }

    public static String buildAutoTagMapFilename(){
        return FileUtils.TEMP_FOLDER.getName() + File.separator + AUTO_TAG_MAP_FILENAME;
    }

    public static List<File> getBackupDataFiles(){
        File settings = FileUtils.getFile(Settings.FILENAME);
        File restaurantsFile = FileUtils.exportToFile(BackupUtils.buildRestaurantFilename(), RestaurantDaoService.selectAll());
        File selfFoodsFile = FileUtils.exportToFile(BackupUtils.buildSelfFoodFilename(), SelfMadeFoodService.selectAll());
        File autoTagMapFile = FileUtils.exportToFile(BackupUtils.buildAutoTagMapFilename(), AutoTagMapperDaoService.selectAll());
        return Lists.newArrayList(settings, restaurantsFile, selfFoodsFile, autoTagMapFile);
    }
}