package personalprojects.seakyluo.randommenu.utils;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import personalprojects.seakyluo.randommenu.R;

public class IntentUtils {

    public static void shareFile(Context context, File file){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileUtils.getFileUri(context, file.getAbsolutePath()));
        context.startActivity(Intent.createChooser(shareIntent, String.format(context.getString(R.string.share_item), file.getName())));
    }
}
