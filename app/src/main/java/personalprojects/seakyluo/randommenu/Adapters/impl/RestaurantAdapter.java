package personalprojects.seakyluo.randommenu.adapters.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.EditConsumeRecordActivity;
import personalprojects.seakyluo.randommenu.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.helpers.SpacesItemDecoration;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {

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
        view.setOnClickListener(v -> {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, EditRestaurantActivity.class);
            intent.putExtra(EditRestaurantActivity.DATA, data.getId());
            activity.startActivityForResult(intent, EditRestaurantActivity.CODE);
            activity.overridePendingTransition(R.anim.push_down_in, 0);
        });
    }

    private void fillWithData(View view, RestaurantFoodAdapter foodAdapter, RestaurantVO data){
        TextView restaurantName = view.findViewById(R.id.restaurant_name);
        TextView foodTypeTextView = view.findViewById(R.id.food_type);
        TextView averagePrice = view.findViewById(R.id.average_price);
        TextView addressTextView = view.findViewById(R.id.address);
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
            addressTextView.setText(addressList.stream().map(Address::buildSimpleAddress).collect(Collectors.joining("\n")));
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
