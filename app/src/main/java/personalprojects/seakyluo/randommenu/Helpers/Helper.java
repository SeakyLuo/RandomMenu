package personalprojects.seakyluo.randommenu.helpers;

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
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedInputStream;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class Helper {
    public static final String ROOT_FOLDER = "RandomMenu";
    public static File root, SaveImageFolder, ImageFolder, TempFolder, TempUnzipFolder, ExportedDataFolder, LogFolder;
    public static Bitmap DefaultFoodImage;
    private static Random random = new Random();

    public static void Log(String content){
        writeFile("Logs/" + Helper.Timestamp() + ".txt" , content);
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
    public static void init(Context context){
        DefaultFoodImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.food_image_place_holder);
        root = createOrOpenFolder(ROOT_FOLDER);
        ImageFolder = createOrOpenFolder("RandomMenuFood");
        SaveImageFolder = createOrOpenFolder("SavedImages");
        TempFolder = createOrOpenFolder("Temp");
        TempUnzipFolder = createOrOpenFolder("TempUnzipFiles");
        ExportedDataFolder = createOrOpenFolder("ExportedData");
        LogFolder = createOrOpenFolder("Logs");
        String settings = readFile(Settings.FILENAME);
        Settings.settings = isNullOrEmpty(settings) ? new Settings() : Settings.fromJson(settings);
        if (isNullOrEmpty(settings)) Log("Empty Settings Created");
    }
    public static String getFilename(String path) { return new File(path).getName(); }
    public static boolean isNullOrEmpty(String string) { return string == null || string.equals(""); }
    public static boolean isBlank(String string){
        return isNullOrEmpty(string.trim());
    }
    public static void loadImage(RequestManager glide, String path, ImageView imageView){
        if (isNullOrEmpty(path)) imageView.setImageBitmap(DefaultFoodImage);
        else glide.load(getImagePath(path)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }
    public static Bitmap getFoodBitmap(String path) { return BitmapFactory.decodeFile(getImagePath(path)); }
    public static Bitmap getFoodBitmap(ImageView imageView){ return ((BitmapDrawable) imageView.getDrawable()).getBitmap(); }
    public static boolean saveImage(ImageView imageView, File folder, String filename){
        return saveImage(getFoodBitmap(imageView), folder, filename);
    }
    public static boolean saveImage(Bitmap image, File folder, String filename){
        try (FileOutputStream out = new FileOutputStream(new File(folder, filename))) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (IOException e) {
            Log("IOException: " + e.toString());
            return false;
        } catch (Exception e){
            return false;
        }
    }
    public static boolean contains(String string, String subString){
        return string != null && string.contains(subString);
    }
    public static String Timestamp() { return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); }
    public static String NewImageFileName(){ return Timestamp() + ".jpg"; }
    public static String NewImageFileName(int suffix){ return Timestamp() + "_" + suffix + ".jpg"; }
    public static String getImagePath(String path){ return getPath("RandomMenuFood", path); }
    public static boolean hasEllipSize(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout == null) return false;
        int lines = layout.getLineCount();
        if (lines == 0) return false;
        return layout.getEllipsisCount(lines - 1) > 0;
    }

    public static void save(){
        String settings = Settings.settings.toString(), emptyJson = new Settings().toString();
        if (settings.equals(emptyJson) && !readFile(Settings.FILENAME).equals(emptyJson))
            Log("Trying to overwrite Settings with Empty content.\nStackTrace:" + Arrays.toString(Thread.currentThread().getStackTrace()));
        else{
            writeFile(Settings.FILENAME, settings);
            String backupSettings = "BackupSettings";
            writeFile("Temp/" + backupSettings + Timestamp() + ".json", settings);
            AList<File> temp_settings = new AList<>(TempFolder.listFiles()).find(f -> f.getName().startsWith(backupSettings));
            if (temp_settings.count() > 10){
                temp_settings.sort((o1, o2) -> (int) (o2.lastModified() - o1.lastModified())).after(10).forEach(File::delete);
            }
        }
    }
    public static String getPath(String... paths) {
        StringBuilder sb = new StringBuilder(Environment.getExternalStorageDirectory().getPath() + File.separator + ROOT_FOLDER);
        for (String path: paths) sb.append(File.separator).append(path);
        return sb.toString();
    }

    public static String readFile(String filename) {
        /* It reads file under root folder */
        return fread(getPath(filename));
    }
    private static String fread(String filename){
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
    public static void writeFile(String filename, String content){
        /* It writes file to root folder */
        fwrite(getPath(filename), content);
    }
    private static void fwrite(String filename, String content){
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(content.getBytes());
            out.close();
        } catch (IOException e) {
            Log.e("fuck", "File write failed: " + e.toString());
        }
    }
    public static File createOrOpenFolder(String folderName){
        File folder = folderName.equals(ROOT_FOLDER) ? new File(getPath()) : new File(getPath(folderName));
        return folder.exists() || folder.mkdir() ? folder : null;
    }
    public static void copy(File src, File dst) throws IOException {
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
    public static Uri getFileUri(Context context, String path){
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path));
    }

    public static void clear(){
        Settings.settings = new Settings();
        save();
    }
    public static void zip(String filename, File... files) throws Exception {
        FileOutputStream dest = new FileOutputStream(filename);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        for (File item: files){
            if (item.isDirectory())
                for (File file: item.listFiles())
                    addZipFile(out, file, item.getName() + File.separator + file.getName());
            else
                addZipFile(out, item, item.getName());
        }
        out.close();
    }
    private static void addZipFile(ZipOutputStream out, File file, String path){
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
    public static void unzip(File zipFile, File targetDirectory) throws Exception {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                try (ZipInputStream zis = new ZipInputStream(bis)) {
                    ZipEntry ze;
                    int count;
                    byte[] buffer = new byte[1024];
                    while ((ze = zis.getNextEntry()) != null) {
                        File file = new File(targetDirectory, ze.getName());
                        File dir = ze.isDirectory() ? file : file.getParentFile();
                        if (!dir.isDirectory() && !dir.mkdirs())
                            throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                        if (ze.isDirectory())
                            continue;
                        try (FileOutputStream fout = new FileOutputStream(file)) {
                            while ((count = zis.read(buffer)) != -1)
                                fout.write(buffer, 0, count);
                        }
                    }
                }
            }
        }
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