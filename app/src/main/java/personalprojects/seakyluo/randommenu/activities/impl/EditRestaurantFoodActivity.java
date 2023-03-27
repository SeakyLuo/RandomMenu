package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.fragments.ImageViewerFragment;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;

public class EditRestaurantFoodActivity extends AppCompatActivity {
    public static final String DATA = "RESTAURANT_FOOD";

    private EditText editName, editPrice, editComment;
    private RestaurantFoodVO currentFood;
    private ImageViewerFragment imageViewerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant_food);

        editName = findViewById(R.id.edit_name);
        editPrice = findViewById(R.id.edit_price);
        editComment = findViewById(R.id.edit_comment);
        ImageButton confirmButton = findViewById(R.id.confirm_button);
        ImageButton cancelButton = findViewById(R.id.cancel_button);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            Intent intent = getIntent();
            currentFood = intent.getParcelableExtra(DATA);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.findFragmentById(R.id.imageviewer_fragment);
        } else {
            currentFood = savedInstanceState.getParcelable(DATA);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
        }

        imageViewerFragment.setEditable(true);
        fillFood(currentFood);
        if (currentFood == null){
            currentFood = new RestaurantFoodVO();
        }
        confirmButton.setOnClickListener(this::onConfirm);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void fillFood(RestaurantFoodVO food){
        if (food == null){
            return;
        }
        editName.setText(food.getName());
        imageViewerFragment.setImages(food.getImages(), food.getCover());
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
        dst.setImages(imageViewerFragment.getImages());
        dst.setCover(imageViewerFragment.getCoverImage());
        String price = editPrice.getText().toString();
        if (NumberUtils.isParsable(price)){
            dst.setPrice(Double.parseDouble(price));
        }
        dst.setComment(editComment.getText().toString());
        return dst;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, ImageViewerFragment.TAG, imageViewerFragment);
        outState.putParcelable(DATA, buildFood());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityUtils.spreadOnActivityResult(this, requestCode, resultCode, data);
    }

    private void finishWithData(RestaurantFoodVO data){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
