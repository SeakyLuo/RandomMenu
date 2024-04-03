package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.swipbackhelper.SwipeBackHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordDisplayAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.enums.OperationType;
import personalprojects.seakyluo.randommenu.fragments.HorizontalImageViewFragment;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ShowRestaurantActivity extends SwipeBackActivity {
    public static final String DATA_ID = "RESTAURANT_ID", DATA = "RESTAURANT", OPERATION_TYPE = "OPERATION_TYPE";
    private TextView restaurantNameText, foodTypeText, averagePriceText, restaurantComment, consumeRecordsText, addressText, environmentText;
    private ConsumeRecordDisplayAdapter consumeRecordAdapter;
    private RestaurantVO restaurant;
    private HorizontalImageViewFragment envImageViewFragment;

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
        environmentText = findViewById(R.id.environment_text);
        consumeRecordsText = findViewById(R.id.consume_record_text);
        RecyclerView consumeRecordRecyclerView = findViewById(R.id.consume_record_recycler_view);
        envImageViewFragment = (HorizontalImageViewFragment) getSupportFragmentManager().findFragmentById(R.id.image_viewer_fragment);

        SwipeBackHelper.getCurrentPage(this).setSwipeEdgePercent(0.2f);
        Intent intent = getIntent();
        long restaurantId = intent.getLongExtra(DATA_ID, 0);
        restaurant = intent.getParcelableExtra(DATA);
        envImageViewFragment.setEditable(false);
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
                    intent.putExtra(EditRestaurantActivity.DATA_ID, restaurant.getId());
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
        AskYesNoDialog dialog = new AskYesNoDialog();
        dialog.setMessage("你确定要删除这条探店记录吗？");
        dialog.setYesListener(v -> {
            RestaurantDaoService.delete(restaurant);
            finishWithOperation(restaurant, OperationType.DELETE);
        });
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
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
        consumeRecordAdapter.setShowAddress(src.getAddressList().size() != 1);
        consumeRecordAdapter.setData(records);
        setConsumeRecords();
        List<String> environmentPictures = records.stream().map(ConsumeRecordVO::getEnvironmentPictures)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(environmentPictures)){
            environmentText.setVisibility(View.GONE);
            ActivityUtils.hideFragment(this, envImageViewFragment);
        } else {
            environmentText.setVisibility(View.VISIBLE);
            ActivityUtils.showFragment(this, envImageViewFragment);
            environmentText.setText(String.format("环境（%d）", environmentPictures.size()));
            envImageViewFragment.setData(environmentPictures);
        }
    }

    private void setConsumeRecords(){
        int foodCount = consumeRecordAdapter.getData().stream().mapToInt(r -> r.getFoods().size()).sum();
        int recordSize = consumeRecordAdapter.getItemCount();
        consumeRecordAdapter.setVertical(recordSize == 1 || foodCount <= 7);
        consumeRecordsText.setText(String.format("消费记录（%d）", consumeRecordAdapter.getItemCount()));
    }

    private void finishWithOperation(RestaurantVO data, OperationType operationType){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        intent.putExtra(OPERATION_TYPE, operationType);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT){
            setData(data.getParcelableExtra(EditRestaurantActivity.DATA));
        }
        else if (requestCode == ActivityCodeConstant.SHOW_CONSUME_RECORD){
            OperationType operationType = (OperationType) data.getSerializableExtra(ShowRestaurantActivity.OPERATION_TYPE);
            ConsumeRecordVO recordVO = data.getParcelableExtra(ShowConsumeRecordActivity.DATA);
            if (operationType == OperationType.DELETE){
                consumeRecordAdapter.removeAt(r -> r.getId() == recordVO.getId());
                setConsumeRecords();
            }
        }
    }

}
