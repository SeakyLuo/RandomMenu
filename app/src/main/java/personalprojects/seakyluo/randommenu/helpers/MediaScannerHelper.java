package personalprojects.seakyluo.randommenu.helpers;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

public class MediaScannerHelper {

    public static void scanFile(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path },
                null, // mimeType, 如果你知道文件类型可以更精确地指定
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // 扫描完成时的回调，可以在这里处理扫描后的逻辑
                        Log.d("MediaScannerHelper", "Scanned " + path + ":");
                        Log.d("MediaScannerHelper", "-> uri=" + uri);
                    }
                });
    }
}