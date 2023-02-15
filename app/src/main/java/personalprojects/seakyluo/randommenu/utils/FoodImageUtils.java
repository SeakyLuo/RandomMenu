package personalprojects.seakyluo.randommenu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.helpers.Helper;

public class FoodImageUtils {
    public static final int CAMERA_CODE = 0, GALLERY_CODE = 1, WRITE_STORAGE = 3, FOOD_CODE = 4, CROP_CODE = 5;

    public static Uri CropImage(Activity activity, String imagePath){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.parse(imagePath), "image/*");
            Uri uri = Uri.fromFile(File.createTempFile("tempCrop", ".jpg", Helper.TempFolder));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, CROP_CODE);
            return uri;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Whoops - your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri OpenCamera(Activity activity){
        if (!Helper.checkAndRequestPermission(activity, Manifest.permission.CAMERA, CAMERA_CODE)){
            Toast.makeText(activity, "没有权限使用摄像头", Toast.LENGTH_SHORT).show();
            return null;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Helper.getFileUri(activity, Helper.NewImageFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, CAMERA_CODE);
        return uri;
    }

    public static void OpenGallery(Activity activity){
        if (Helper.checkAndRequestReadStoragePermission(activity)){
            Toast.makeText(activity, "没有权限图库", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_image)), GALLERY_CODE);
    }
}
