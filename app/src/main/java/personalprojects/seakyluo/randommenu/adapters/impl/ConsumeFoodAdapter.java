package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class ConsumeFoodAdapter extends DraggableAdapter<RestaurantFoodVO> {
    private boolean canEdit;

    public ConsumeFoodAdapter(Context context, boolean canEdit){
        this.context = context;
        this.canEdit = canEdit;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_consume_food;
    }

    @Override
    protected void fillViewHolder(CustomAdapter.CustomViewHolder viewHolder, RestaurantFoodVO data, int position) {
        View view = viewHolder.getView();
        TextView foodName = view.findViewById(R.id.food_name);
        TextView foodPrice = view.findViewById(R.id.food_price);
        TextView foodComment = view.findViewById(R.id.food_comment);
        ImageView foodImage = view.findViewById(R.id.food_image);
        ImageButton reorderButton = view.findViewById(R.id.reorder_button);

        data.setIndex(position);
        fillFood(data, foodName, foodPrice, foodComment, foodImage);
        view.setOnClickListener(v -> showFood(data));
        if (!canEdit || getData().size() == 1){
            reorderButton.setVisibility(View.GONE);
        }
        reorderButton.setOnTouchListener((v, event) -> dragStart(viewHolder, event));
    }

    private void showFood(RestaurantFoodVO data) {
        FragmentActivity activity = getContextAsFragmentActivity();
        if (canEdit){
            Intent intent = new Intent(activity, EditRestaurantFoodActivity.class);
            intent.putExtra(EditRestaurantFoodActivity.DATA, data);
            activity.startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT_FOOD);
            activity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        } else {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.setRestaurantFoodId(data.getId());
            dialog.showNow(activity.getSupportFragmentManager(), FoodCardDialog.TAG);
        }
    }

    @Override
    protected void dataSizeChanged() {
        if (canEdit){
            for (CustomViewHolder viewHolder : viewHolders){
                viewHolder.getView().findViewById(R.id.reorder_button).setVisibility(data.size() == 1 ? View.GONE : View.VISIBLE);
            }
        }
    }

    private void fillFood(RestaurantFoodVO data, TextView foodName, TextView foodPrice, TextView foodComment, ImageView foodImage){
        foodName.setText(data.getName());
        String price = "￥" + DoubleUtils.truncateZero(data.getPrice());
        if (data.getQuantity() > 1){
            price += "×" + data.getQuantity();
        }
        foodPrice.setText(price);
        foodComment.setText(data.getComment());
        ImageUtils.loadImage(context, data.getCover(), foodImage);
    }

}
