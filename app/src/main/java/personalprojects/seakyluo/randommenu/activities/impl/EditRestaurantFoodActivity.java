package personalprojects.seakyluo.randommenu.activities.impl;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;
import personalprojects.seakyluo.randommenu.utils.PermissionUtils;

public class EditRestaurantFoodActivity extends AppCompatActivity {
    public static final String DATA = "RESTAURANT_FOOD";

    private ImageView foodImage;
    private ImageButton cameraButton;
    private EditText editName, editPrice, editComment;
    private RestaurantFoodVO currentFood;
    private Uri foodImageUri, cropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant_food);

        foodImage = findViewById(R.id.food_image);
        cameraButton = findViewById(R.id.camera_button);
        editName = findViewById(R.id.edit_name);
        editPrice = findViewById(R.id.edit_price);
        editComment = findViewById(R.id.edit_comment);
        ImageButton confirmButton = findViewById(R.id.confirm_button);
        ImageButton cancelButton = findViewById(R.id.cancel_button);

        if (savedInstanceState == null){
            Intent intent = getIntent();
            currentFood = intent.getParcelableExtra(DATA);
        } else {
            currentFood = savedInstanceState.getParcelable(DATA);
        }
        fillFood(currentFood);
        if (currentFood == null){
            currentFood = new RestaurantFoodVO();
        }
        foodImage.setOnClickListener(v -> startFoodActivity());
        cameraButton.setOnClickListener(v -> showMenuFlyout());
        confirmButton.setOnClickListener(this::onConfirm);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void startFoodActivity(){
        String uri = currentFood.getPictureUri();
        if (StringUtils.isEmpty(uri)){
            return;
        }
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra(FullScreenImageActivity.IMAGE, Lists.newArrayList(uri));
        startActivity(intent);
    }

    private void fillFood(RestaurantFoodVO food){
        if (food == null){
            return;
        }
        String pictureUri = food.getPictureUri();
        foodImageUri = StringUtils.isEmpty(pictureUri) ? null : Uri.parse(ImageUtils.getImagePath(pictureUri));
        ImageUtils.loadImage(this, pictureUri, foodImage);
        editName.setText(food.getName());
        editPrice.setText(DoubleUtils.truncateZero(food.getPrice()));
        editComment.setText(food.getComment());
    }

    private void onConfirm(View view){
        if (StringUtils.isEmpty(editName.getText().toString())){
            Toast.makeText(this, "名称不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        String price = editPrice.getText().toString();
        if (StringUtils.isEmpty(price)){
            editPrice.setText("0");
        }
        else if (!NumberUtils.isParsable(price)){
            Toast.makeText(this, "价格不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        finishWithData(buildFood());
    }

    private RestaurantFoodVO buildFood(){
        RestaurantFoodVO dst = JsonUtils.copy(currentFood);
        dst.setName(editName.getText().toString());
        String price = editPrice.getText().toString();
        if (NumberUtils.isParsable(price)){
            dst.setPrice(Double.parseDouble(price));
        }
        dst.setComment(editComment.getText().toString());
        return dst;
    }

    private void showMenuFlyout(){
        if (!PermissionUtils.checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityCodeConstant.WRITE_STORAGE)){
            return;
        }
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, this, cameraButton);
        if (foodImageUri == null) helper.removeItems(R.id.edit_image_item);
        helper.removeItems(R.id.set_cover_item, R.id.move_to_first_item, R.id.remove_image_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    foodImageUri = ImageUtils.openCamera(this);
                    return foodImageUri != null;
                case R.id.open_gallery_item:
                    ImageUtils.openGallery(this);
                    return true;
                case R.id.edit_image_item:
                    cropImageUri = ImageUtils.cropImage(this, foodImageUri.getPath());
                    return cropImageUri != null;
            }
            return false;
        });
        helper.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA, buildFood());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            return;
        switch (requestCode){
            case ActivityCodeConstant.WRITE_STORAGE:
                showMenuFlyout();
                break;
            case ActivityCodeConstant.CAMERA:
                ImageUtils.openCamera(this);
                break;
            case ActivityCodeConstant.READ_EXTERNAL_STORAGE_CODE:
                ImageUtils.openGallery(this, false);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String pictureUri;
        switch (requestCode){
            case ActivityCodeConstant.CAMERA:
                if (!ImageUtils.saveImage(this, foodImageUri, pictureUri = foodImageUri.getPath())){
                    return;
                }
                break;
            case ActivityCodeConstant.GALLERY:
                ClipData clipData = data.getClipData();
                if (clipData == null) {
                    foodImageUri = data.getData();
                } else {
                    int count = clipData.getItemCount();
                    if (count == 0) return;
                    if (count > 1) Toast.makeText(this, "只会使用第一张图哦", Toast.LENGTH_SHORT).show();
                    foodImageUri = clipData.getItemAt(0).getUri();
                }
                if (!ImageUtils.saveImage(this, foodImageUri, pictureUri = ImageUtils.newImageFileName())){
                    return;
                }
                break;
            case ActivityCodeConstant.CROP_IMAGE:
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), cropImageUri);
                    if (!ImageUtils.saveImage(image, Helper.ImageFolder, pictureUri = ImageUtils.newImageFileName())){
                        return;
                    }
                } catch (Exception e){
                    Log.e(EditRestaurantFoodActivity.class.getName(), "onActivityResult failed", e);
                    return;
                }
                break;
            default:
                return;
        }
        ImageUtils.loadImage(this, pictureUri, foodImage);
        currentFood.setPictureUri(pictureUri);
    }

    private void finishWithData(RestaurantFoodVO data){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
