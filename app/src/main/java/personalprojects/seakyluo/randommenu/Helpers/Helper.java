package personalprojects.seakyluo.randommenu.helpers;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Layout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class Helper {
    public static final int READ_EXTERNAL_STORAGE_CODE = 1;
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
        if (StringUtils.isEmpty(settings)) Log("Empty Settings Created");
    }

    public static String formatCurrentTimestamp() { return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); }

    public static boolean hasEllipSize(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout == null) return false;
        int lines = layout.getLineCount();
        if (lines == 0) return false;
        return layout.getEllipsisCount(lines - 1) > 0;
    }

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

    public static List<Tag> guessTags(String food){
        HashSet<Tag> tags = new HashSet<>();
        Settings.settings.AutoTagMap.entrySet().forEach(e -> {
            if (food.contains(e.getKey())){
                e.getValue().forEach(t -> tags.add(new Tag(t)));
            }
        });
        return new ArrayList<>(tags);
    }
}