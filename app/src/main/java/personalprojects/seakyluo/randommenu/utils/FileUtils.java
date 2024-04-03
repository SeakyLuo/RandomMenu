package personalprojects.seakyluo.randommenu.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.exceptions.ResourcedException;

public class FileUtils {

    public static final String ROOT_FOLDER_NAME = "RandomMenu", IMAGE_FOLDER_NAME = "RandomMenuFood";
    public static File ROOT_FOLDER;
    public static File SAVED_IMAGE_FOLDER;
    public static File IMAGE_FOLDER;
    public static File TEMP_FOLDER;
    public static File TEMP_UNZIP_FOLDER;
    public static File EXPORTED_DATA_FOLDER;
    public static File LOG_FOLDER;

    public static void init(Activity activity){
        ROOT_FOLDER = createOrOpenFolder(activity, ROOT_FOLDER_NAME);
        IMAGE_FOLDER = createOrOpenFolder(activity, IMAGE_FOLDER_NAME);
        SAVED_IMAGE_FOLDER = createOrOpenFolder(activity, "SavedImages");
        TEMP_FOLDER = createOrOpenFolder(activity, "Temp");
        TEMP_UNZIP_FOLDER = createOrOpenFolder(activity, "TempUnzipFiles");
        EXPORTED_DATA_FOLDER = createOrOpenFolder(activity, "ExportedData");
        LOG_FOLDER = createOrOpenFolder(activity, "Logs");
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getPath(String... paths) {
        StringBuilder sb = new StringBuilder(ROOT_FOLDER == null ? ROOT_FOLDER_NAME : ROOT_FOLDER.getAbsolutePath());
        for (String path: paths){
            sb.append(File.separator).append(path);
        }
        return sb.toString();
    }

    public static File getFile(String path){
        return new File(getPath(path));
    }

    /**
     *  It reads file under root folder
     **/
    public static String readFile(String filename) {
        return fread(null, getPath(filename));
    }
    public static String readFile(Activity activity, String filename) {
        return fread(activity, getPath(filename));
    }
    private static String fread(Activity activity, String filename){
        try (FileReader fr = new FileReader(filename)){
            return readFile(fr);
        } catch (FileNotFoundException e) {
            if (activity != null){
                if (!PermissionUtils.checkAndRequestReadStoragePermission(activity)) {
                    Log.e("fuck", "File not found: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
    public static String readFile(Activity activity, File file){
        try (FileReader fr = new FileReader(file)){
            return readFile(fr);
        } catch (FileNotFoundException e) {
            if (activity != null){
                if (!PermissionUtils.checkAndRequestReadStoragePermission(activity)) {
                    Log.e("fuck", "File not found: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private static String readFile(FileReader fr){
        try (BufferedReader reader = new BufferedReader(fr)) {
            StringBuilder builder = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e("fuck", "Can not read file: " + e);
        }
        return "";
    }

    public static File exportToFile(String filename, Object data){
        writeFile(filename, JsonUtils.toJson(data));
        return getFile(filename);
    }

    public static void writeFile(String filename, String content){
        /* It writes file to root folder */
        fwrite(getPath(filename), content);
    }
    private static void fwrite(String filename, String content){
        try (FileOutputStream out = new FileOutputStream(filename)) {
            out.write(content.getBytes());
        } catch (IOException e) {
            Log.e("fuck", "File write failed: " + e);
        }
    }
    public static File createOrOpenFolder(Context context, String folderName){
        File folder;
        if (Build.VERSION.SDK_INT > 29){
            File externalFilesDir = context.getExternalFilesDir(null);
            String path = folderName.equals(ROOT_FOLDER_NAME) ? ROOT_FOLDER_NAME : ROOT_FOLDER_NAME + File.separator + folderName;
            folder = new File(externalFilesDir, path);
        } else {
            String path = folderName.equals(ROOT_FOLDER_NAME) ? getPath() : getPath(folderName);
            folder = new File(path);
        }
        return folder.exists() || folder.mkdir() ? folder : null;
    }

    public static void copyFile(File src, File dst) throws IOException {
        if (!dst.isDirectory()){
            Log.w("copyFile", "dst " + dst.getPath() + " is not a directory");
            return;
        }
        try (InputStream in = new FileInputStream(src)) {
            File dstDirectory;
            if (src.isFile()){
                dstDirectory = new File(dst, src.getName());
            } else {
                dstDirectory = dst;
            }
            try (OutputStream out = new FileOutputStream(dstDirectory)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static void copyDirectory(File src, File dst) throws IOException {
        if (!dst.isDirectory()){
            Log.w("copyDirectory", "dst " + dst.getPath() + " is not a directory");
            return;
        }
        if (!dst.exists()) {
            dst.mkdirs(); // 创建目标目录
        }

        // 获取源目录下的所有文件和文件夹
        File[] items = src.listFiles();
        if (items != null) {
            for (File item : items) {
                File destFile = new File(dst, item.getName());
                if (item.isDirectory()) {
                    // 如果是文件夹，则递归调用自身进行复制
                    copyDirectory(item, destFile);
                } else {
                    // 如果是文件，则直接复制
                    copyFile(item, destFile);
                }
            }
        }
    }


    public static Uri getFileUri(Context context, String path){
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path));
    }

    public static File zip(File folder, String filename, List<File> files) {
        String path = folder.getAbsolutePath() + File.separator + filename;
        try (FileOutputStream dest = new FileOutputStream(path)){
            try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))){
                for (File item: files){
                    if (item.isDirectory()){
                        for (File file: item.listFiles()){
                            addZipFile(out, file, item.getName() + File.separator + file.getName());
                        }
                    }
                    else {
                        addZipFile(out, item, item.getName());
                    }
                }
            }
        } catch (FileNotFoundException e){
            BackupUtils.Log("zip FileNotFoundException:" + e);
            Log.w("zip FileNotFoundException:", e);
            throw new ResourcedException(R.string.file_not_found, e);
        } catch (Exception e) {
            BackupUtils.Log("zip Exception:" + e);
            Log.w("zip FileNotFoundException:", e);
            throw new ResourcedException(R.string.export_data_failed, e);
        }
        return new File(folder, filename);
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

    public static void unzip(InputStream fis, File targetDirectory) throws IOException {
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

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            unzip(fis, targetDirectory);
        }
    }

}
