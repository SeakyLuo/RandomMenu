package personalprojects.seakyluo.randommenu.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DeviceUtils {

    public static boolean isFoldableScreen(Context context){
        return isFoldableScreen(context.getResources().getDisplayMetrics());
    }

    public static boolean isFoldableScreen(DisplayMetrics displayMetrics){
        return displayMetrics.widthPixels > displayMetrics.heightPixels * 0.6;
    }
}
