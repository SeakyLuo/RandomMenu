package personalprojects.seakyluo.randommenu.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.R;

public class Helper {
    public static File ImageFolder, TempFolder;
    public static Context context;
    public static Bitmap DefaultFoodImage;
//    private static HashMap<String, Bitmap> foodImageCache = new HashMap<>();
    private static Random random = new Random();

    public static Bitmap Screenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    public static int RandRange(int start, int end) { return random.nextInt((end - start)) + start; }
    public static void Init(Context context){
        Helper.context = context;
        DefaultFoodImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.food_image_place_holder);
        ImageFolder = CreateOrOpenFolder("RandomMenuFood");
        TempFolder = CreateOrOpenFolder("RandomMenuTemp");
        String settings = ReadJson(context, Settings.FILENAME);
        Settings.settings = IsNullOrEmpty(settings) ? new Settings() : Settings.FromJson(settings);
    }

    public static boolean IsNullOrEmpty(String string) { return string == null || string.equals(""); }
    public static void LoadImage(RequestManager glide, String path, ImageView imageView){
        if (IsNullOrEmpty(path)) imageView.setImageBitmap(DefaultFoodImage);
        else glide.load(path).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//        Bitmap image = foodImageCache.getOrDefault(path, null);
//        if (image == null) {
//            glide.load(path).into(imageView);
////            foodImageCache.put(path, GetFoodBitmap(imageView));
//        }else{
//            imageView.setImageBitmap(image);
//        }
    }
    public static Bitmap GetFoodBitmap(String path) { return BitmapFactory.decodeFile(path); }
    public static Bitmap GetFoodBitmap(ImageView imageView){ return ((BitmapDrawable) imageView.getDrawable()).getBitmap(); }
    public static String SaveImage(ImageView imageView, File folder, String filename){
        return SaveImage(GetFoodBitmap(imageView), folder, filename);
    }
    public static String SaveImage(Bitmap image, File folder, String filename){
        String image_path = new File(folder, filename).getPath();
        try (FileOutputStream out = new FileOutputStream(image_path)) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            image_path = "";
        }
        return image_path;
    }
    public static String NewImageFileName(){ return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg"; }
    public static File TempCopy(String src, String prefix, String suffix){
        try (InputStream in = new FileInputStream(src)) {
            File file = File.createTempFile(prefix, suffix, TempFolder);
            try (OutputStream out = new FileOutputStream(file)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String GetImagePath(File folder, String image){
        return new File(folder, image).getPath();
    }

    public static void Save(Context context){
        SaveJson(context, Settings.FILENAME, Settings.settings.ToJson());
    }

    public static String ReadJson(Context context, String filename) {
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
    public static void SaveJson(Context context, String filename, String json){
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

    public static void Clear(Context context){
        Settings.settings = new Settings();
        Save(context);
        for (File file: ImageFolder.listFiles())
            file.delete();
    }
}
