package personalprojects.seakyluo.randommenu.adapters.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.ShowRestaurantActivity;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.constants.EmojiConstant;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.TextViewUtils;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {

    @Setter
    private String keyword;

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
        foodAdapter.setOnFoodClickListener((vh, f) -> showFood(f));
        fillWithData(view, foodAdapter, data);
        view.setOnClickListener(v -> showRestaurant(data));
        view.setOnLongClickListener(v -> {
            editRestaurant(data);
            return true;
        });
    }

    private void showFood(RestaurantFoodVO data){
        FragmentActivity activity = getContextAsFragmentActivity();
        FoodCardDialog dialog = new FoodCardDialog();
        dialog.setRestaurantFoodId(data.getId());
        dialog.showNow(activity.getSupportFragmentManager(), FoodCardDialog.TAG);
    }

    private void showRestaurant(RestaurantVO data){
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, ShowRestaurantActivity.class);
        intent.putExtra(ShowRestaurantActivity.DATA_ID, data.getId());
        activity.startActivityForResult(intent, ActivityCodeConstant.SHOW_RESTAURANT);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    private void editRestaurant(RestaurantVO data){
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, EditRestaurantActivity.class);
        intent.putExtra(EditRestaurantActivity.DATA_ID, data.getId());
        activity.startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT);
        activity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    private void fillWithData(View view, RestaurantFoodAdapter foodAdapter, RestaurantVO data){
        TextView restaurantName = view.findViewById(R.id.restaurant_name);
        TextView foodTypeTextView = view.findViewById(R.id.food_type);
        TextView averagePrice = view.findViewById(R.id.average_price);
        ExpandableTextView addressTextView = view.findViewById(R.id.address);
        TextView commentText = view.findViewById(R.id.comment_text);

        restaurantName.setText(data.getName());
        TextViewUtils.highlightTextView(restaurantName, keyword);
        FoodType foodType = data.getFoodType();
        if (foodType == null){
            foodTypeTextView.setVisibility(View.GONE);
        } else {
            foodTypeTextView.setVisibility(View.VISIBLE);
            foodTypeTextView.setText(foodType.getName());
        }
        averagePrice.setText("人均￥" + DoubleUtils.truncateZero(data.getAverageCost()));
        List<AddressVO> addressList = data.getAddressList();
        if (addressList.isEmpty()){
            addressTextView.setVisibility(View.GONE);
        } else {
            addressTextView.setVisibility(View.VISIBLE);
            String address = addressList.stream().map(AddressVO::buildSimpleAddress).collect(Collectors.joining("\n"));
            addressTextView.setText(address);
        }
        String comment = data.getComment();
        if (StringUtils.isBlank(comment)){
            commentText.setVisibility(View.GONE);
        } else {
            commentText.setVisibility(View.VISIBLE);
            commentText.setText(EmojiConstant.COMMENT + " 评价：" + comment);
        }
        TextViewUtils.highlightTextView(commentText, keyword);
        foodAdapter.setKeyword(keyword);
        foodAdapter.setData(data.getFoods());
    }

}
