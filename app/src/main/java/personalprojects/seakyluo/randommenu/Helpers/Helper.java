package personalprojects.seakyluo.randommenu.Helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.R;

public class Helper {
    public static final String ROOT_FOLDER = "RandomMenu";
    public static File Root, SaveImageFolder, ImageFolder, TempFolder, ExportedDataFolder, LogFolder;
    public static Bitmap DefaultFoodImage;
    private static Random random = new Random();

    public static void Log(String content){
        WriteFile("Logs/" + Helper.Timestamp() + ".txt" , content);
    }
    public static void CopyToClipboard(Context context, String text){
        CopyToClipboard(context, text, text);
    }
    public static void CopyToClipboard(Context context, String label, String text){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
    public static Bitmap Screenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    public static int RandRange(int start, int end) { return random.nextInt((end - start)) + start; }
    public static void Init(Context context){
        DefaultFoodImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.food_image_place_holder);
        Root = CreateOrOpenFolder(ROOT_FOLDER);
        ImageFolder = CreateOrOpenFolder("RandomMenuFood");
        SaveImageFolder = CreateOrOpenFolder("SavedImages");
        TempFolder = CreateOrOpenFolder("Temp");
        ExportedDataFolder = CreateOrOpenFolder("ExportedData");
        LogFolder = CreateOrOpenFolder("Logs");
        String settings = ReadFile(Settings.FILENAME);
        Settings.settings = IsNullOrEmpty(settings) ? new Settings() : Settings.FromJson(settings);
        if (IsNullOrEmpty(settings)) Log("Empty Settings Created");
    }
    public static String GetFilename(String path) { return new File(path).getName(); }
    public static boolean IsNullOrEmpty(String string) { return string == null || string.equals(""); }
    public static void LoadImage(RequestManager glide, String path, ImageView imageView){
        if (IsNullOrEmpty(path)) imageView.setImageBitmap(DefaultFoodImage);
        else glide.load(GetImagePath(path)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }
    public static Bitmap GetFoodBitmap(String path) { return BitmapFactory.decodeFile(GetImagePath(path)); }
    public static Bitmap GetFoodBitmap(ImageView imageView){ return ((BitmapDrawable) imageView.getDrawable()).getBitmap(); }
    public static boolean SaveImage(ImageView imageView, File folder, String filename){
        return SaveImage(GetFoodBitmap(imageView), folder, filename);
    }
    public static boolean SaveImage(Bitmap image, File folder, String filename){
        try (FileOutputStream out = new FileOutputStream(new File(folder, filename))) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e){
            return false;
        }
    }
    public static String Timestamp() { return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); }
    public static String NewImageFileName(){ return Timestamp() + ".jpg"; }
    public static String NewImageFileName(int suffix){ return Timestamp() + "_" + suffix + ".jpg"; }
    public static String GetImagePath(String path){ return getPath("RandomMenuFood", path); }

    public static void Save(){
        String settings = Settings.settings.ToJson(), emptyJson = new Settings().ToJson();
        if (settings.equals(emptyJson) && !ReadFile(Settings.FILENAME).equals(emptyJson))
            Log("Trying to overwrite Settings with Empty content.");
        else
            WriteFile(Settings.FILENAME, settings);
    }
    public static String getPath(String... paths) {
        StringBuilder sb = new StringBuilder(Environment.getExternalStorageDirectory().getPath() + File.separator + ROOT_FOLDER);
        for (String path: paths) sb.append(File.separator).append(path);
        return sb.toString();
    }

    public static String ReadFile(String filename) {
        /* It reads file under root folder */
        return Fread(getPath(filename));
    }
    private static String Fread(String filename){
        String string = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append('\n');
            reader.close();
            return builder.toString();
        } catch (FileNotFoundException e) {
            Log.e("fuck", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("fuck", "Can not read file: " + e.toString());
        }
        return string;
    }
    public static void WriteFile(String filename, String content){
        /* It writes file to root folder */
        Fwrite(getPath(filename), content);
    }
    private static void Fwrite(String filename, String content){
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(content.getBytes());
            out.close();
        } catch (IOException e) {
            Log.e("fuck", "File write failed: " + e.toString());
        }
    }
    public static File CreateOrOpenFolder(String folderName){
        File folder = folderName.equals(ROOT_FOLDER) ? new File(getPath()) : new File(getPath(folderName));
        return folder.exists() || folder.mkdir() ? folder : null;
    }
    public static void Copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
    public static Uri GetFileUri(Context context, String path){
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path));
    }

    public static void Clear(){
        Settings.settings = new Settings();
        Save();
    }
    public static boolean Zip(String filename, File... files){
        FileOutputStream dest = null;
        try {
            dest = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            return false;
        }
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        for (File item: files){
            if (item.isDirectory())
                for (File file: item.listFiles())
                    AddZipFile(out, file, item.getName() + File.separator + file.getName());
            else
                AddZipFile(out, item, item.getName());
        }
        try {
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    private static void AddZipFile(ZipOutputStream out, File file, String path){
        byte[] data = new byte[1024];
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return;
        }
        try {
            out.putNextEntry(new ZipEntry(path));
            int len;
            while ((len = in.read(data)) > 0)
                out.write(data, 0, len);
            out.closeEntry();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
