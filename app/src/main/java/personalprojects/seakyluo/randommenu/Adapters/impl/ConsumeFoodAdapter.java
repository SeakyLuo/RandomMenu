package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.dialogs.RestaurantFoodDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;

public class ConsumeFoodAdapter extends DraggableAdapter<RestaurantFoodVO> {
    @Setter
    private DataItemClickedListener<RestaurantFoodVO> clickedListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_consume_food;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, RestaurantFoodVO data, int position) {
        View view = viewHolder.getView();
        TextView foodName = view.findViewById(R.id.food_name);
        TextView foodPrice = view.findViewById(R.id.food_price);
        TextView foodComment = view.findViewById(R.id.food_comment);
        ImageView foodImage = view.findViewById(R.id.food_image);
        ImageButton reorderButton = view.findViewById(R.id.reorder_button);

        fillFood(data, foodName, foodPrice, foodComment, foodImage);
        view.setOnClickListener(v -> {
            RestaurantFoodDialog dialog = new RestaurantFoodDialog();
            dialog.setFood(data);
            dialog.setConfirmListener(food -> {
                data.copyFrom(food);
                fillFood(data, foodName, foodPrice, foodComment, foodImage);
                if (clickedListener != null){
                    clickedListener.click(viewHolder, data);
                }
            });
            dialog.showNow(((FragmentActivity)context).getSupportFragmentManager(), RestaurantFoodDialog.TAG);
        });
        if (getData().size() == 1){
            reorderButton.setVisibility(View.GONE);
        }
        reorderButton.setOnTouchListener((v, event) -> dragStart(viewHolder, event));
    }

    @Override
    protected void dataSizeChanged() {
        for (CustomViewHolder viewHolder : viewHolders){
            viewHolder.getView().findViewById(R.id.reorder_button).setVisibility(data.size() == 1 ? View.GONE : View.VISIBLE);
        }
    }

    private void fillFood(RestaurantFoodVO data, TextView foodName, TextView foodPrice, TextView foodComment, ImageView foodImage){
        foodName.setText(data.getName());
        Double price = data.getPrice();
        if (price != null){
            foodPrice.setText("￥" + price);
        }
        foodComment.setText(data.getComment());
        Helper.loadImage(Glide.with(context), data.getPictureUri(), foodImage);
    }

}
