package personalprojects.seakyluo.randommenu;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.FoodImageUtils;

public class EditRestaurantFoodActivity extends AppCompatActivity {
    public static int CODE = 1;
    public static final String DATA = "RESTAURANT_FOOD", IS_DRAFT = "IsDraft";

    private ImageView foodImage;
    private ImageButton cameraButton;
    private EditText editName, editPrice, editComment;
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

        RestaurantFoodVO food;
        if (savedInstanceState == null){
            food = getIntent().getParcelableExtra(DATA);
        } else {
            food = savedInstanceState.getParcelable(DATA);
        }
        fillFood(food);
        cameraButton.setOnClickListener(v -> {
            if (Helper.checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, FoodImageUtils.WRITE_STORAGE)){
                showMenuFlyout();
            }
        });
        confirmButton.setOnClickListener(this::onConfirm);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void fillFood(RestaurantFoodVO food){
        if (food == null){
            return;
        }
        Helper.loadImage(Glide.with(this), food.getPictureUri(), foodImage);
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
        RestaurantFoodVO dst = new RestaurantFoodVO();
        if (foodImageUri != null){
            dst.setPictureUri(foodImageUri.getPath());
        }
        dst.setName(editName.getText().toString());
        String price = editPrice.getText().toString();
        if (NumberUtils.isParsable(price)){
            dst.setPrice(Double.parseDouble(price));
        }
        dst.setComment(editComment.getText().toString());
        return dst;
    }

    private void showMenuFlyout(){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, this, cameraButton);
        if (foodImageUri == null) helper.removeItems(R.id.edit_image_item);
        helper.removeItems(R.id.set_cover_item, R.id.move_to_first_item, R.id.remove_image_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    foodImageUri = FoodImageUtils.OpenCamera(this);
                    return foodImageUri != null;
                case R.id.open_gallery_item:
                    FoodImageUtils.OpenGallery(this);
                    return true;
                case R.id.edit_image_item:
                    foodImageUri = FoodImageUtils.CropImage(this, foodImageUri.getPath());
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
            case FoodImageUtils.WRITE_STORAGE:
                showMenuFlyout();
                break;
            case FoodImageUtils.CAMERA_CODE:
                FoodImageUtils.OpenCamera(this);
                break;
            case Helper.READ_EXTERNAL_STORAGE_CODE:
                FoodImageUtils.OpenGallery(this);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bitmap image;
        switch (requestCode){
            case FoodImageUtils.CAMERA_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), foodImageUri);
                    Helper.saveImage(image, Helper.ImageFolder, foodImageUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case FoodImageUtils.GALLERY_CODE:
                try {
                    Uri uri;
                    ClipData clipData = data.getClipData();
                    if (clipData == null) {
                        uri = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        Helper.saveImage(image, Helper.ImageFolder, foodImageUri.getPath());
                    }else{
                        int count = clipData.getItemCount();
                        for (int i = 0; i < count; i++){
                            uri = clipData.getItemAt(i).getUri();
                            if (Helper.saveImage(MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                    Helper.ImageFolder,
                                    Helper.NewImageFileName(i))){
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
        }
    }

    private void finishWithData(RestaurantFoodVO data){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
