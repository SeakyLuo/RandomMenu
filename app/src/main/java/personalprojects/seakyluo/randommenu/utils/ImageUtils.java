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
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.util.concurrent.ListenableFuture;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;

public class ImageUtils {
    public static Bitmap DEFAULT_FOOD_IMAGE;
    public static int CAMERA_CALLER, GALLERY_CALLER;

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
        return FileUtils.getPath(FileUtils.IMAGE_FOLDER_NAME, path);
    }

    public static String newImageFileName(){
        return BackupUtils.now() + ".jpg";
    }
    public static String newImageFileName(Integer suffix){
        if (suffix == null) return newImageFileName();
        return BackupUtils.now() + "_" + suffix + ".jpg";
    }

    public static Bitmap getFoodBitmap(String path) { return BitmapFactory.decodeFile(getImagePath(path)); }
    public static Bitmap getFoodBitmap(ImageView imageView){ return ((BitmapDrawable) imageView.getDrawable()).getBitmap(); }

    public static boolean saveImage(Context context, Uri uri, String filename){
        try {
            Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return ImageUtils.saveImage(image, FileUtils.IMAGE_FOLDER, filename);
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
            BackupUtils.Log("IOException: " + e);
            return false;
        } catch (Exception e){
            return false;
        }
    }

    public static Uri cropImage(Activity activity, String imagePath){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(FileProvider.getUriForFile(activity, "personalprojects.seakyluo.randommenu.provider", new File(imagePath)), "image/*");
            Uri uri = Uri.fromFile(File.createTempFile("tempCrop", ".jpg", FileUtils.TEMP_FOLDER));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
        return openCamera(activity, null);
    }

    public static Uri openCamera(Activity activity, View caller){
        GALLERY_CALLER = caller == null ? 0 : caller.getId();
        if (!PermissionUtils.checkAndRequestPermission(activity, Manifest.permission.CAMERA, ActivityCodeConstant.CAMERA)){
            Toast.makeText(activity, "没有权限使用摄像头", Toast.LENGTH_SHORT).show();
            return null;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = FileUtils.getFileUri(activity, ImageUtils.newImageFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, ActivityCodeConstant.CAMERA);
        return uri;
    }

    public static void openCameraX(Activity activity, PreviewView previewView){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(activity);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, preview);
            } catch (Exception e) {
                // Handle the exception according to your app's requirements.
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    public static void openGallery(Activity activity){
        openGallery(activity, null);
    }

    public static void openGallery(Activity activity, View caller){
        GALLERY_CALLER = caller == null ? 0 : caller.getId();
        if (!PermissionUtils.checkAndRequestReadStoragePermission(activity)){
            Toast.makeText(activity, "没有权限使用图库", Toast.LENGTH_SHORT).show();
            return;
        }
//        openGalleryNew(activity, caller, null);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_image)), ActivityCodeConstant.GALLERY);
    }

    public static void openGalleryNew(Activity activity, View caller, ArrayList<String> imagePaths){
        GALLERY_CALLER = caller == null ? 0 : caller.getId();
        if (!PermissionUtils.checkAndRequestReadStoragePermission(activity)){
            Toast.makeText(activity, "没有权限使用图库", Toast.LENGTH_SHORT).show();
            return;
        }
        PictureSelector.create(activity)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia m : result){
                            ExifInterface exifInterface;
                            try {
                                Uri uri = Uri.parse(m.getPath());
                                String path = FileUtils.getPath(activity, uri);
                                exifInterface = new ExifInterface(path);
                                AddressVO address = AddressUtils.getAddress(exifInterface, activity);
                                System.out.println(address);
                            } catch (Exception e) {
                                Log.w("buildRestaurantFromImages", "ExifInterface", e);
                                exifInterface = null;
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}
