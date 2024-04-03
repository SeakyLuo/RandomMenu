package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.EditConsumeRecordActivity;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeFoodAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.constants.EmojiConstant;
import personalprojects.seakyluo.randommenu.database.services.ConsumeRecordDaoService;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.enums.OperationType;
import personalprojects.seakyluo.randommenu.fragments.HorizontalImageViewFragment;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ShowConsumeRecordActivity extends SwipeBackActivity {
    public static final String DATA = "CONSUME_RECORD", OPERATION_TYPE = "OPERATION_TYPE";
    public static final String EATER_DELIMITER = "、";
    private TextView consumeTimeText, addressText, eatersText, consumeRecordComment, consumeTotalCost, restaurantName, foodText, environmentText;
    private ConsumeFoodAdapter foodAdapter;
    private ConsumeRecordVO currentRecord;
    private HorizontalImageViewFragment envImageViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_consume_record);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton moreButton = findViewById(R.id.more_button);
        consumeTimeText = findViewById(R.id.consume_time_text);
        addressText = findViewById(R.id.address_text);
        eatersText = findViewById(R.id.eaters_text);
        consumeRecordComment = findViewById(R.id.consume_record_comment);
        consumeTotalCost = findViewById(R.id.consume_total_cost);
        restaurantName = findViewById(R.id.restaurant_name);
        foodText = findViewById(R.id.food_text);
        environmentText = findViewById(R.id.environment_text);
        RecyclerView foodRecyclerView = findViewById(R.id.food_recycler_view);
        foodAdapter = new ConsumeFoodAdapter(this, false);
        envImageViewFragment = (HorizontalImageViewFragment) getSupportFragmentManager().findFragmentById(R.id.image_viewer_fragment);

        Intent intent = getIntent();
        currentRecord = intent.getParcelableExtra(DATA);
        foodRecyclerView.setAdapter(foodAdapter);
        envImageViewFragment.setEditable(false);
        setData(currentRecord);
        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(this::showMoreMenu);
    }

    private void showMoreMenu(View view){
        PopupMenuHelper helper = new PopupMenuHelper(R.menu.show_restaurant_menu, this, view);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.edit:
                    Intent intent = new Intent(this, EditConsumeRecordActivity.class);
                    intent.putExtra(EditConsumeRecordActivity.DATA, currentRecord);
                    startActivityForResult(intent, ActivityCodeConstant.EDIT_CONSUME_RECORD);
                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                    return true;
                case R.id.delete_item:
                    deleteConsumeRecord();
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private void deleteConsumeRecord(){
        AskYesNoDialog dialog = new AskYesNoDialog();
        dialog.setMessage("你确定要删除这条消费记录吗？");
        dialog.setYesListener(v -> {
            ConsumeRecordDaoService.delete(currentRecord);
            finishWithOperation(currentRecord, OperationType.DELETE);
        });
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
    }

    private void finishWithOperation(ConsumeRecordVO data, OperationType operationType){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        intent.putExtra(OPERATION_TYPE, operationType);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setData(ConsumeRecordVO src){
        if (src == null){
            return;
        }
        consumeTimeText.setText(EmojiConstant.CONSUME_TIME + " 时间：" + DateFormatUtils.format(src.getConsumeTime(), ConsumeRecordVO.CONSUME_TIME_FORMAT));
        consumeTotalCost.setText(EmojiConstant.TOTAL_COST + " 总消费：￥" + DoubleUtils.truncateZero(src.getTotalCost()));
        List<String> eaters = src.getEaters();
        if (CollectionUtils.isEmpty(eaters)){
            eatersText.setVisibility(View.GONE);
        } else {
            eatersText.setVisibility(View.VISIBLE);
            eatersText.setText(EmojiConstant.EATERS + " 饭伙：" + String.join(EATER_DELIMITER, eaters));
        }
        addressText.setText(EmojiConstant.ADDRESS + " 地址：" + src.getAddress().buildFullAddress());
        String comment = src.getComment();
        if (StringUtils.isEmpty(comment)){
            consumeRecordComment.setVisibility(View.GONE);
        } else {
            consumeRecordComment.setVisibility(View.VISIBLE);
            consumeRecordComment.setText(EmojiConstant.COMMENT + " 评价：" + comment);
        }
        RestaurantVO restaurant = RestaurantDaoService.selectById(src.getRestaurantId());
        if (restaurant != null){
            restaurantName.setText(restaurant.getName());
        }
        foodText.setText(String.format("菜品（%d）", src.getFoods().size()));
        foodAdapter.setData(src.getFoods());
        List<String> environmentPictures = src.getEnvironmentPictures();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_CONSUME_RECORD){
            ConsumeRecordVO consumeRecord = data.getParcelableExtra(EditConsumeRecordActivity.DATA);
            setData(consumeRecord);
            ConsumeRecordDaoService.update(consumeRecord);
        }
    }
}
