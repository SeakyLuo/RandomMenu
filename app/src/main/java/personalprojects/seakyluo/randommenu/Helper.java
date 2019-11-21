package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Helper {
    public static File ImageFolder;

    public static void Init(){
        ImageFolder = CreateOrOpenFolder("Food");
        String settings = ReadJson(Settings.FILENAME);
        Settings.settings = IsNullOrEmpty(settings) ? new Settings() : Settings.FromJson(settings);
    }

    public static boolean IsNullOrEmpty(String string) { return string == null || string.equals(""); }

    public static Bitmap GetFoodBitmap(Food food){
        return GetFoodBitmap(food.ImagePath);
    }
    public static Bitmap GetFoodBitmap(String path){
        return BitmapFactory.decodeFile(path);
    }

    public static String GetImagePath(String image){
        return new File(Environment.getExternalStorageDirectory(), image).getPath();
    }

    public static void Save(){
        SaveJson(Settings.FILENAME, Settings.settings.ToJson());
    }

    public static String ReadJson(String filename) {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        if (!file.exists()) return "";
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(bytes);
    }
    public static void SaveJson(String filename, String json){
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {

        }
        try {
            stream.write(json.getBytes());
        } catch (IOException e) {

        } finally {
            try {
                stream.close();
            } catch (IOException e) {

            }
        }
    }
    public static File CreateOrOpenFolder(String folderName){
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
        boolean success = folder.exists() || folder.mkdirs();
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
        return folder;
    }
}
