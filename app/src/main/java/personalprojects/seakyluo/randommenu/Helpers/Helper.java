package personalprojects.seakyluo.randommenu.helpers;

import android.app.Activity;
import android.text.Layout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.TagMapEntry;
import personalprojects.seakyluo.randommenu.services.SelfFoodService;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class Helper {
    public static final String ROOT_FOLDER = "RandomMenu";
    public static File root, SaveImageFolder, ImageFolder, TempFolder, TempUnzipFolder, ExportedDataFolder, LogFolder;

    public static void Log(String content){
        FileUtils.writeFile("Logs/" + Helper.formatCurrentTimestamp() + ".txt" , content);
    }

    public static void init(Activity activity){
        ImageUtils.init(activity);
        root = FileUtils.createOrOpenFolder(ROOT_FOLDER);
        AppDatabase.createInstance(activity);
        ImageFolder = FileUtils.createOrOpenFolder("RandomMenuFood");
        SaveImageFolder = FileUtils.createOrOpenFolder("SavedImages");
        TempFolder = FileUtils.createOrOpenFolder("Temp");
        TempUnzipFolder = FileUtils.createOrOpenFolder("TempUnzipFiles");
        ExportedDataFolder = FileUtils.createOrOpenFolder("ExportedData");
        LogFolder = FileUtils.createOrOpenFolder("Logs");
        String settings = FileUtils.readFile(activity, Settings.FILENAME);
        Settings.settings = StringUtils.isEmpty(settings) ? new Settings() : Settings.fromJson(settings);
        script(Settings.settings);
        if (StringUtils.isEmpty(settings)) Log("Empty Settings Created");
    }

    private static void script(Settings settings){
//        SelfFoodService.selectAll().forEach(f -> SelfFoodDaoService.update(f));
//        settings.AutoTagMap.entrySet().forEach(e -> AutoTagMapperDaoService.insert(new TagMapEntry() {{ setKeyword(e.getKey()); setTagsFromString(e.getValue()); }}));
//        settings.Foods.forEach(f -> {
//            Food food = SelfFoodService.selectByName(f.Name);
//            if (food != null){
//                SelfFoodService.removeFood(food);
//            }
//        });
//        FoodTagDaoService.selectAll().forEach(t -> {
//            FoodTagService.delete(t);
//        });
//        settings.Foods.reverse().forEach(f -> {
//            HashSet<String> fav = new HashSet<>(settings.MyFavorites);
//            f.Favorite = fav.contains(f.Name);
//            SelfFoodService.addFood(f.convert());
//        });
    }

    public static String formatCurrentTimestamp() { return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); }

    public static void save(){
        String settings = Settings.settings.toString(), emptyJson = new Settings().toString();
        if (settings.equals(emptyJson) && !FileUtils.readFile(Settings.FILENAME).equals(emptyJson))
            Log("Trying to overwrite Settings with Empty content.\nStackTrace:" + Arrays.toString(Thread.currentThread().getStackTrace()));
        else{
            FileUtils.writeFile(Settings.FILENAME, settings);
            String backupSettings = "BackupSettings";
            FileUtils.writeFile("Temp/" + backupSettings + formatCurrentTimestamp() + ".json", settings);
            AList<File> temp_settings = new AList<>(TempFolder.listFiles()).find(f -> f.getName().startsWith(backupSettings));
            if (temp_settings.size() > 10){
                temp_settings.sorted((o1, o2) -> (int) (o2.lastModified() - o1.lastModified())).after(10).ForEach(File::delete);
            }
        }
    }

    public static void clear(){
        Settings.settings = new Settings();
        save();
    }

}