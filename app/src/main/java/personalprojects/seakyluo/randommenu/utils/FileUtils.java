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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import personalprojects.seakyluo.randommenu.helpers.Helper;

import static personalprojects.seakyluo.randommenu.helpers.Helper.ROOT_FOLDER;

public class FileUtils {

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
        StringBuilder sb = new StringBuilder(Environment.getExternalStorageDirectory().getPath()).append(File.separator).append(ROOT_FOLDER);
        for (String path: paths) sb.append(File.separator).append(path);
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder builder = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()){
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            if (activity != null){
                if (!PermissionUtils.checkAndRequestReadStoragePermission(activity)) {
                    Log.e("fuck", "File not found: " + e);
                }
            }
        } catch (IOException e) {
            Log.e("fuck", "Can not read file: " + e);
        }
        return "";
    }

    public static String getFilename(String path) { return new File(path).getName(); }

    public static void writeFile(String filename, String content){
        /* It writes file to root folder */
        fwrite(getPath(filename), content);
    }
    private static void fwrite(String filename, String content){
        try (FileOutputStream out = new FileOutputStream(filename);) {
            out.write(content.getBytes());
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


}
