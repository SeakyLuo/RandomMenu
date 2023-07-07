package personalprojects.seakyluo.randommenu.helpers;

import android.app.Activity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class Helper {

    public static void Log(String content){
        FileUtils.writeFile("Logs/" + Helper.formatCurrentTimestamp() + ".txt" , content);
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

    public static String formatCurrentTimestamp() { return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd_HHmmss"); }

    public static void save(){
        String settings = Settings.settings.toString(), emptyJson = new Settings().toString();
        if (settings.equals(emptyJson) && !FileUtils.readFile(Settings.FILENAME).equals(emptyJson)){
            Log("Trying to overwrite Settings with Empty content.\nStackTrace:" + Arrays.toString(Thread.currentThread().getStackTrace()));
            return;
        }
        FileUtils.writeFile(Settings.FILENAME, settings);
        String backupSettings = "BackupSettings";
        FileUtils.writeFile("Temp/" + backupSettings + formatCurrentTimestamp() + ".json", settings);
        File[] tempFiles = FileUtils.TEMP_FOLDER.listFiles();
        if (tempFiles == null){
            return;
        }
        // 保留最新10个备份
        Arrays.stream(tempFiles).filter(f -> f.getName().startsWith(backupSettings))
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .skip(10)
                .forEach(File::delete);
    }

    public static void clear(){
        Settings.settings = new Settings();
        save();
    }

}