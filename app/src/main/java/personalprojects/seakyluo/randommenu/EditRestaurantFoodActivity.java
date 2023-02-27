package personalprojects.seakyluo.randommenu;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;
import personalprojects.seakyluo.randommenu.utils.PermissionUtils;

public class EditRestaurantFoodActivity extends AppCompatActivity {
    public static int CODE = 1;
    public static final String DATA = "RESTAURANT_FOOD";

    private ImageView foodImage;
    private ImageButton cameraButton;
    private EditText editName, editPrice, editComment;
    private RestaurantFoodVO currentFood;
    private Uri foodImageUri;

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
            currentFood = getIntent().getParcelableExtra(DATA);
        } else {
            currentFood = savedInstanceState.getParcelable(DATA);
        }
        fillFood(currentFood);
        if (currentFood == null){
            currentFood = new RestaurantFoodVO();
        }
        cameraButton.setOnClickListener(v -> showMenuFlyout());
        confirmButton.setOnClickListener(this::onConfirm);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void fillFood(RestaurantFoodVO food){
        if (food == null){
            return;
        }
        ImageUtils.loadImage(this, food.getPictureUri(), foodImage);
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
        if (!PermissionUtils.checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, ImageUtils.WRITE_STORAGE)){
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
                    foodImageUri = ImageUtils.cropImage(this, foodImageUri.getPath());
                    return foodImageUri != null;
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
            case ImageUtils.WRITE_STORAGE:
                showMenuFlyout();
                break;
            case ImageUtils.CAMERA_CODE:
                ImageUtils.openCamera(this);
                break;
            case Helper.READ_EXTERNAL_STORAGE_CODE:
                ImageUtils.openGallery(this, false);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bitmap image;
        String pictureUri;
        switch (requestCode){
            case ImageUtils.CAMERA_CODE:
                image = ImageUtils.saveImage(this, foodImageUri, pictureUri = foodImageUri.getPath());
                break;
            case ImageUtils.GALLERY_CODE:
                ClipData clipData = data.getClipData();
                if (clipData == null) {
                    foodImageUri = data.getData();
                } else {
                    int count = clipData.getItemCount();
                    if (count == 0) return;
                    foodImageUri = clipData.getItemAt(0).getUri();
                }
                image = ImageUtils.saveImage(this, foodImageUri, pictureUri = ImageUtils.newImageFileName());
                break;
            default:
                return;
        }
        foodImage.setImageBitmap(image);
        currentFood.setPictureUri(pictureUri);
    }

    private void finishWithData(RestaurantFoodVO data){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
