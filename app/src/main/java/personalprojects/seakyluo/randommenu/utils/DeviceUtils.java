package personalprojects.seakyluo.randommenu.utils;

import android.util.DisplayMetrics;

public class DeviceUtils {

    public static boolean isFoldableScreen(DisplayMetrics displayMetrics){
        return displayMetrics.widthPixels > displayMetrics.heightPixels * 0.6;
    }
}
