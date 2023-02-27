package personalprojects.seakyluo.randommenu.adapters.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import personalprojects.seakyluo.randommenu.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {

    private static final String EXPAND_TEXT = "展开", COLLAPSE_TEXT = "收起";
    private static final int MAX_ADDRESS_LINES = 1;

    public RestaurantAdapter(Context context){
        this.context = context;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_restaurant;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, RestaurantVO data, int position) {
        View view = viewHolder.getView();
        RecyclerView restaurantFoodRecyclerView = view.findViewById(R.id.restaurant_food_list_recycler_view);
        RestaurantFoodAdapter foodAdapter = new RestaurantFoodAdapter(context);

        restaurantFoodRecyclerView.setAdapter(foodAdapter);
        fillWithData(view, foodAdapter, data);
        view.setOnClickListener(v -> editRestaurant(data));
        view.setOnLongClickListener(v -> {
            editRestaurant(data);
            return true;
        });
    }

    private void editRestaurant(RestaurantVO data){
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, EditRestaurantActivity.class);
        intent.putExtra(EditRestaurantActivity.DATA, data.getId());
        activity.startActivityForResult(intent, EditRestaurantActivity.CODE);
        activity.overridePendingTransition(R.anim.push_down_in, 0);
    }

    private void fillWithData(View view, RestaurantFoodAdapter foodAdapter, RestaurantVO data){
        TextView restaurantName = view.findViewById(R.id.restaurant_name);
        TextView foodTypeTextView = view.findViewById(R.id.food_type);
        TextView averagePrice = view.findViewById(R.id.average_price);
        ExpandableTextView addressTextView = view.findViewById(R.id.address);
        TextView commentTextView = view.findViewById(R.id.comment);

        restaurantName.setText(data.getName());
        FoodType foodType = data.getFoodType();
        if (foodType == null){
            foodTypeTextView.setVisibility(View.GONE);
        } else {
            foodTypeTextView.setVisibility(View.VISIBLE);
            foodTypeTextView.setText(foodType.getName());
        }
        averagePrice.setText("人均￥" + DoubleUtils.truncateZero(data.getAverageCost()));
        List<Address> addressList = data.getAddressList();
        if (addressList.isEmpty()){
            addressTextView.setVisibility(View.GONE);
        } else {
            addressTextView.setVisibility(View.VISIBLE);
            String address = addressList.stream().map(Address::buildSimpleAddress).collect(Collectors.joining("\n"));
            addressTextView.setText(address);
        }
        String comment = data.getComment();
        if (StringUtils.isBlank(comment)){
            commentTextView.setVisibility(View.GONE);
        } else {
            commentTextView.setVisibility(View.VISIBLE);
            commentTextView.setText(comment);
        }
        foodAdapter.setData(data.getFoods());
    }

}
