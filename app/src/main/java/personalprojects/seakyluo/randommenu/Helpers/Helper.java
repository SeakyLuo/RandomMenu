package personalprojects.seakyluo.randommenu.helpers;

import android.app.Activity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
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
//        SelfFoodDaoService.selectAll().forEach(f -> {
//            List<String> paths = SelfFoodImageDaoService.selectByFood(f.getId());
//            ImagePathService.insertSelfMadeFood(f.getId(), paths);
//        });
//        RestaurantDaoService.selectAll().forEach(r -> {
//            r.getRecords().forEach(recordVO -> {
//                recordVO.getFoods().forEach(f -> {
//                    ImagePathService.insertRestaurantFood(f.getId(), Lists.newArrayList(f.getCover()));
//                });
//            });
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