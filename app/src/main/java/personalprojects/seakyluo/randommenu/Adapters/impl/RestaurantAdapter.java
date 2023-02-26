package personalprojects.seakyluo.randommenu.adapters.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import personalprojects.seakyluo.randommenu.EditConsumeRecordActivity;
import personalprojects.seakyluo.randommenu.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_restaurant;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, RestaurantVO data, int position) {
        View view = viewHolder.getView();
        RecyclerView restaurantFoodRecyclerView = view.findViewById(R.id.restaurant_food_list_recycler_view);
        RestaurantFoodAdapter foodAdapter = new RestaurantFoodAdapter();

        restaurantFoodRecyclerView.setAdapter(foodAdapter);
        fillWithData(view, foodAdapter, data);
        view.setOnClickListener(v -> {
            Activity activity = (Activity) context;
            Intent intent = new Intent(context, EditRestaurantActivity.class);
            intent.putExtra(EditRestaurantActivity.DATA, data);
            activity.startActivityForResult(intent, EditRestaurantActivity.CODE);
            activity.overridePendingTransition(R.anim.push_down_in, 0);
        });
    }

    private void fillWithData(View view, RestaurantFoodAdapter foodAdapter, RestaurantVO data){
        TextView restaurantName = view.findViewById(R.id.restaurant_name);
        TextView foodType = view.findViewById(R.id.food_type);
        TextView averagePrice = view.findViewById(R.id.average_price);
        TextView address = view.findViewById(R.id.address);
        TextView commentTextView = view.findViewById(R.id.comment);

        restaurantName.setText(data.getName());
        foodType.setText(data.getFoodTypeName());
        averagePrice.setText("人均￥" + data.computeAveragePrice());
        address.setText(data.getAddressList().get(0).buildSimpleAddress());
        String comment = data.getComment();
        if (StringUtils.isBlank(comment)){
            commentTextView.setVisibility(View.GONE);
        } else {
            commentTextView.setVisibility(View.VISIBLE);
            commentTextView.setText(comment);
        }
        foodAdapter.setData(data.computeFoodsToShow());
    }
}
