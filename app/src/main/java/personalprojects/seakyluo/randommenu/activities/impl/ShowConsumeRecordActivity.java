package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;
import java.util.Optional;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeFoodAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class ShowConsumeRecordActivity extends SwipeBackActivity {
    public static final String DATA = "CONSUME_RECORD";
    public static final String EATER_DELIMITER = "、";
    private TextView consumeTimeText, addressText, eatersText, consumeRecordComment, consumeTotalCost, restaurantName, foodText;
    private ConsumeFoodAdapter foodAdapter;
    private ConsumeRecordVO currentRecord;

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
        RecyclerView foodRecyclerView = findViewById(R.id.food_recycler_view);
        foodAdapter = new ConsumeFoodAdapter(this, false);

        Intent intent = getIntent();
        currentRecord = intent.getParcelableExtra(DATA);
        foodRecyclerView.setAdapter(foodAdapter);
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
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private void setData(ConsumeRecordVO src){
        if (src == null){
            return;
        }
        consumeTimeText.setText("\uD83D\uDCC5 时间：" + DateFormatUtils.format(src.getConsumeTime(), ConsumeRecordVO.CONSUME_TIME_FORMAT));
        consumeTotalCost.setText("\uD83D\uDCB0 总消费：￥" + DoubleUtils.truncateZero(src.getTotalCost()));
        List<String> eaters = src.getEaters();
        if (CollectionUtils.isEmpty(eaters)){
            eatersText.setVisibility(View.GONE);
        } else {
            eatersText.setVisibility(View.VISIBLE);
            eatersText.setText("\uD83E\uDD62 饭伙：" + String.join(EATER_DELIMITER, eaters));
        }
        addressText.setText("\uD83D\uDCCD 地址：" + src.getAddress().buildFullAddress());
        String comment = src.getComment();
        if (StringUtils.isEmpty(comment)){
            consumeRecordComment.setVisibility(View.GONE);
        } else {
            consumeRecordComment.setVisibility(View.VISIBLE);
            consumeRecordComment.setText(comment);
        }
        RestaurantVO restaurant = RestaurantDaoService.selectById(src.getRestaurantId());
        if (restaurant != null){
            restaurantName.setText(restaurant.getName());
        }
        foodText.setText(String.format("菜品（%d）", src.getFoods().size()));
        foodAdapter.setData(src.getFoods());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_CONSUME_RECORD){

        }
    }
}
