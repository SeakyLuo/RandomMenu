package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
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

        foodName.setText(data.getName());
        Double price = data.getPrice();
        if (price != null){
            foodPrice.setText("￥" + price);
        }
        foodComment.setText(data.getComment());
        Helper.loadImage(Glide.with(view), data.getPictureUri(), foodImage);
        reorderButton.setOnTouchListener((v, e) -> dragStart(viewHolder, e));
    }

}
