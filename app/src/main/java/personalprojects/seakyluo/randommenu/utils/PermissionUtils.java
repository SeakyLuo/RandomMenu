package personalprojects.seakyluo.randommenu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final int READ_EXTERNAL_STORAGE_CODE = 1;

    public static boolean checkAndRequestReadStoragePermission(Activity activity){
        return checkAndRequestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
    }
    public static boolean checkAndRequestPermission(Activity activity, String permission, int code){
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED){
            activity.requestPermissions(new String[]{ permission }, code);
            return false;
        } else {
            return true;
        }
    }
}
