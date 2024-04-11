package personalprojects.seakyluo.randommenu.adapters.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.EditConsumeRecordActivity;
import personalprojects.seakyluo.randommenu.activities.impl.ShowConsumeRecordActivity;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.constants.EmojiConstant;
import personalprojects.seakyluo.randommenu.database.services.AddressDaoService;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardBottomDialog;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ConsumeRecordDisplayAdapter extends CustomAdapter<ConsumeRecordVO> {

    private long restaurantId;
    @Setter
    private boolean showAddress = false;
    @Setter
    private boolean vertical = true;

    public ConsumeRecordDisplayAdapter(Context context, long restaurantId){
        this.context = context;
        this.restaurantId = restaurantId;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_show_listed_consume_record;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, ConsumeRecordVO data, int position) {
        View view = viewHolder.getView();

        TextView eatersText = view.findViewById(R.id.eaters_text);
        TextView addressText = view.findViewById(R.id.address_text);
        TextView consumeTime = view.findViewById(R.id.consume_time);
        TextView consumeTotalCost = view.findViewById(R.id.consume_total_cost);
        TextView consumeRecordComment = view.findViewById(R.id.consume_record_comment);
        RecyclerView foodRecyclerViewHorizontal = view.findViewById(R.id.food_recycler_view_horizontal);
        RecyclerView foodRecyclerViewVertical = view.findViewById(R.id.food_recycler_view_vertical);
        RestaurantFoodAdapter restaurantFoodAdapter = new RestaurantFoodAdapter(context);
        ConsumeFoodAdapter consumeFoodAdapter = new ConsumeFoodAdapter(context, false);

        String eaters = formatEater(data.getEaters());
        if (StringUtils.isEmpty(eaters)){
            eatersText.setVisibility(View.GONE);
        } else {
            eatersText.setVisibility(View.VISIBLE);
            eatersText.setText(EmojiConstant.EATERS + " 饭伙：" + eaters);
        }
        addressText.setText(EmojiConstant.ADDRESS + " 地址：" + data.getAddress().buildSimpleAddress());
        if (showAddress){
            addressText.setVisibility(View.VISIBLE);
        } else {
            addressText.setVisibility(View.GONE);
        }
        consumeTime.setText(data.formatConsumeTimeToDay());
        consumeTotalCost.setText(EmojiConstant.TOTAL_COST + " 总消费：￥" + DoubleUtils.truncateZero(data.getTotalCost()));
        String comment = data.getComment();
        if (StringUtils.isEmpty(comment)){
            consumeRecordComment.setVisibility(View.GONE);
        } else {
            consumeRecordComment.setVisibility(View.VISIBLE);
            consumeRecordComment.setText(EmojiConstant.COMMENT + " 评价： " + comment);
        }
        List<RestaurantFoodVO> foods = data.getFoods();
        if (vertical){
            consumeFoodAdapter.setData(foods);
            foodRecyclerViewVertical.setAdapter(consumeFoodAdapter);
        } else {
            restaurantFoodAdapter.setData(foods);
            foodRecyclerViewHorizontal.setAdapter(restaurantFoodAdapter);
        }

        view.setOnClickListener(v -> showConsumeRecord(data));
        view.setOnLongClickListener(v -> {
            Activity activity = (Activity) this.context;
            Intent intent = new Intent(activity, EditConsumeRecordActivity.class);
            intent.putExtra(EditConsumeRecordActivity.DATA, data);
            intent.putParcelableArrayListExtra(EditConsumeRecordActivity.ADDRESS_LIST, (ArrayList<AddressVO>) AddressDaoService.selectByRestaurant(restaurantId));
            activity.startActivityForResult(intent, ActivityCodeConstant.EDIT_CONSUME_RECORD);
            activity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            return true;
        });
        restaurantFoodAdapter.setOnFoodClickListener((vh, d) -> {
            FragmentActivity activity = getContextAsFragmentActivity();
            FoodCardBottomDialog dialog = new FoodCardBottomDialog();
            dialog.setRestaurantFoodId(d.getId());
            dialog.showNow(activity.getSupportFragmentManager(), FoodCardBottomDialog.TAG);
        });
    }

    private void showConsumeRecord(ConsumeRecordVO data) {
        Activity activity = (Activity) this.context;
        Intent intent = new Intent(activity, ShowConsumeRecordActivity.class);
        intent.putExtra(ShowConsumeRecordActivity.DATA, data);
        activity.startActivityForResult(intent, ActivityCodeConstant.SHOW_CONSUME_RECORD);
        activity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    private String formatEater(List<String> eaters){
        if (CollectionUtils.isEmpty(eaters)){
            return null;
        }
        String eatersStr = String.join("、", eaters);
        if (eatersStr.length() < 15){
            return eatersStr;
        }
        return eaters.stream().limit(4).collect(Collectors.joining("、")) + "等" + eaters.size() + "人";
    }

}
