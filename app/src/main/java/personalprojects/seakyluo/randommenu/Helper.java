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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Helper {
    public static File ImageFolder;
    public static Context context;

    public static void Init(Context context){
        Helper.context = context;
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
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append('\n');
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Helper", "File not found: " + e.toString());
        }
        catch (IOException e) {
            Log.e("Helper", "Can not read file: " + e.toString());
        }
        return ret;
    }
    public static void SaveJson(String filename, String json){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
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
