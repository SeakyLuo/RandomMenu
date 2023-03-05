package personalprojects.seakyluo.randommenu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.helpers.Helper;

public class ImageUtils {
    public static Bitmap DEFAULT_FOOD_IMAGE;

    public static void init(Activity activity){
        DEFAULT_FOOD_IMAGE = BitmapFactory.decodeResource(activity.getResources(), R.drawable.food_image_place_holder);
    }

    public static Bitmap screenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private static void loadImage(RequestManager glide, String path, ImageView imageView){
        if (StringUtils.isEmpty(path)){
            imageView.setImageBitmap(DEFAULT_FOOD_IMAGE);
        }
        else {
            glide.load(getImagePath(path)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
    }

    public static void loadImage(Context context, String path, ImageView imageView){
        loadImage(Glide.with(context), path, imageView);
    }

    public static void loadImage(View view, String path, ImageView imageView){
        loadImage(Glide.with(view), path, imageView);
    }

    public static String getImagePath(String path){
        return FileUtils.getPath("RandomMenuFood", path);
    }

    public static String newImageFileName(){ return Helper.formatCurrentTimestamp() + ".jpg"; }
    public static String newImageFileName(int suffix){ return Helper.formatCurrentTimestamp() + "_" + suffix + ".jpg"; }

    public static Bitmap getFoodBitmap(String path) { return BitmapFactory.decodeFile(getImagePath(path)); }
    public static Bitmap getFoodBitmap(ImageView imageView){ return ((BitmapDrawable) imageView.getDrawable()).getBitmap(); }

    public static boolean saveImage(Context context, Uri uri, String filename){
        try {
            Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return ImageUtils.saveImage(image, Helper.ImageFolder, filename);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean saveImage(Bitmap image, File folder, String filename){
        try (FileOutputStream out = new FileOutputStream(new File(folder, filename))) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (IOException e) {
            Helper.Log("IOException: " + e.toString());
            return false;
        } catch (Exception e){
            return false;
        }
    }

    public static Uri cropImage(Activity activity, String imagePath){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.parse(imagePath), "image/*");
            Uri uri = Uri.fromFile(File.createTempFile("tempCrop", ".jpg", Helper.TempFolder));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, ActivityCodeConstant.CROP_IMAGE);
            return uri;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Whoops - your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri openCamera(Activity activity){
        if (!PermissionUtils.checkAndRequestPermission(activity, Manifest.permission.CAMERA, ActivityCodeConstant.CAMERA)){
            Toast.makeText(activity, "没有权限使用摄像头", Toast.LENGTH_SHORT).show();
            return null;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileUtils.getFileUri(activity, ImageUtils.newImageFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, ActivityCodeConstant.CAMERA);
        return uri;
    }

    public static void openGallery(Activity activity){
        openGallery(activity, true);
    }

    public static void openGallery(Activity activity, boolean allowMultiple){
        if (!PermissionUtils.checkAndRequestReadStoragePermission(activity)){
            Toast.makeText(activity, "没有权限使用图库", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple);
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_image)), ActivityCodeConstant.GALLERY);
    }
}
