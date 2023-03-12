package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordDisplayAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordEditAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.AddressVO;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ShowRestaurantActivity extends SwipeBackActivity {
    public static final String DATA_ID = "RESTAURANT_ID", DATA = "RESTAURANT";
    private TextView restaurantNameText, foodTypeText, averagePriceText, restaurantComment, consumeRecordsText, addressText;
    private ConsumeRecordDisplayAdapter consumeRecordAdapter;
    private RestaurantVO restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_restaurant);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton moreButton = findViewById(R.id.more_button);
        restaurantNameText = findViewById(R.id.restaurant_name);
        addressText = findViewById(R.id.address_text);
        foodTypeText = findViewById(R.id.food_type);
        averagePriceText = findViewById(R.id.average_price);
        restaurantComment = findViewById(R.id.restaurant_comment);
        consumeRecordsText = findViewById(R.id.consume_record_text);
        RecyclerView consumeRecordRecyclerView = findViewById(R.id.consume_record_recycler_view);

        long restaurantId;
        if (savedInstanceState == null){
            restaurantId = getIntent().getLongExtra(DATA_ID, 0);
            restaurant = getIntent().getParcelableExtra(DATA);
        } else {
            restaurantId = savedInstanceState.getLong(DATA_ID, 0);
            restaurant = savedInstanceState.getParcelable(DATA);
        }
        consumeRecordAdapter = new ConsumeRecordDisplayAdapter(this, restaurantId);
        restaurant = Optional.ofNullable(restaurant).orElse(RestaurantDaoService.selectById(restaurantId));
        consumeRecordRecyclerView.setAdapter(consumeRecordAdapter);

        setData(restaurant);
        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(this::showMoreMenu);
    }

    private void showMoreMenu(View view){
        PopupMenuHelper helper = new PopupMenuHelper(R.menu.show_restaurant_menu, this, view);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.edit:
                    Intent intent = new Intent(this, EditRestaurantActivity.class);
                    intent.putExtra(EditRestaurantActivity.DATA, restaurant);
                    startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT);
                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                    return true;
                case R.id.delete_item:
                    deleteRestaurant();
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private void deleteRestaurant(){
        // TODO
    }

    private void setData(RestaurantVO src){
        if (src == null){
            return;
        }
        restaurantNameText.setText(src.getName());
        addressText.setText(src.getAddressList().stream().map(AddressVO::buildFullAddress).collect(Collectors.joining("\n")));
        FoodType foodType = src.getFoodType();
        if (foodType != null){
            foodTypeText.setText(src.getFoodType().getName());
        }
        double averageCost = src.getAverageCost();
        if (averageCost == 0){
            averagePriceText.setVisibility(View.GONE);
        } else {
            averagePriceText.setText("人均￥" + DoubleUtils.truncateZero(src.getAverageCost()));
            averagePriceText.setVisibility(View.VISIBLE);
        }
        String comment = src.getComment();
        if (StringUtils.isEmpty(comment)){
            restaurantComment.setVisibility(View.GONE);
        } else {
            restaurantComment.setText("评价：" + comment);
            restaurantComment.setVisibility(View.VISIBLE);
        }
        List<ConsumeRecordVO> records = src.getRecords();
        records.sort(Comparator.comparing(ConsumeRecordVO::getConsumeTime).reversed());
        consumeRecordAdapter.setData(records);
        consumeRecordsText.setText(String.format("消费记录（%d）", records.size()));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        setData(data.getParcelableExtra(EditRestaurantFoodActivity.DATA));
    }

}
