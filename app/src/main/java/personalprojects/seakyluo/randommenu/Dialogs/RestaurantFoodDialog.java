package personalprojects.seakyluo.randommenu.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.interfaces.DataOperationListener;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.PermissionUtils;

import static android.app.Activity.RESULT_OK;

public class RestaurantFoodDialog extends DialogFragment {
    public static final String TAG = "RestaurantFoodDialog";

    @Setter
    private RestaurantFoodVO food;
    @Setter
    private DataOperationListener<RestaurantFoodVO> confirmListener;

    private ImageView foodImage;
    private ImageButton cameraButton;
    private EditText editName, editPrice, editComment;
    private Uri foodImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_restaurant_food, container,false);
        foodImage = view.findViewById(R.id.food_image);
        cameraButton = view.findViewById(R.id.camera_button);
        editName = view.findViewById(R.id.edit_name);
        editPrice = view.findViewById(R.id.edit_price);
        editComment = view.findViewById(R.id.edit_comment);
        Button confirmButton = view.findViewById(R.id.confirm_button);

        fillFood(food);
        cameraButton.setOnClickListener(v -> {
            if (PermissionUtils.checkAndRequestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, ImageUtils.WRITE_STORAGE)){
                showMenuFlyout();
            }
        });
        confirmButton.setOnClickListener(this::onConfirm);
        return view;
    }

    private void fillFood(RestaurantFoodVO food){
        if (food == null){
            editPrice.setText("0.0");
            return;
        }
        ImageUtils.loadImage(getContext(), food.getPictureUri(), foodImage);
        editName.setText(food.getName());
        editPrice.setText(DoubleUtils.truncateZero(food.getPrice()));
        editComment.setText(food.getComment());
    }

    private void onConfirm(View view){
        if (StringUtils.isEmpty(editName.getText().toString())){
            Toast.makeText(getContext(), "名称不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NumberUtils.isParsable(editPrice.getText().toString())){
            Toast.makeText(getContext(), "价格不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        confirmListener.operate(buildFood());
        dismiss();
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
        Activity activity = getActivity();
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, getContext(), cameraButton);
        if (foodImageUri == null) helper.removeItems(R.id.edit_image_item);
        helper.removeItems(R.id.set_cover_item, R.id.move_to_first_item, R.id.remove_image_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    foodImageUri = ImageUtils.openCamera(activity);
                    return foodImageUri != null;
                case R.id.open_gallery_item:
                    ImageUtils.openGallery(activity);
                    return true;
                case R.id.edit_image_item:
                    foodImageUri = ImageUtils.cropImage(activity, foodImageUri.getPath());
                    return foodImageUri != null;
            }
            return false;
        });
        helper.show();
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
                ImageUtils.openCamera(getActivity());
                break;
            case Helper.READ_EXTERNAL_STORAGE_CODE:
                ImageUtils.openGallery(getActivity());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case ImageUtils.CAMERA_CODE:
                break;
            case ImageUtils.GALLERY_CODE:
                break;
            case ImageUtils.CROP_CODE:
                break;
        }
    }
}