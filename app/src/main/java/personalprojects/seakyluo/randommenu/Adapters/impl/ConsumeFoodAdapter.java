package personalprojects.seakyluo.randommenu.adapters.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.EditConsumeRecordActivity;
import personalprojects.seakyluo.randommenu.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.dialogs.RestaurantFoodDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ConsumeFoodAdapter extends DraggableAdapter<RestaurantFoodVO> {
    @Setter
    private DataItemClickedListener<RestaurantFoodVO> clickedListener;

    public ConsumeFoodAdapter(Context context){
        this.context = context;
    }

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
            Activity activity = (Activity) context;
            Intent intent = new Intent(context, EditRestaurantFoodActivity.class);
            intent.putExtra(EditRestaurantFoodActivity.DATA, data);
            activity.startActivityForResult(intent, EditRestaurantFoodActivity.CODE);
            activity.overridePendingTransition(R.anim.push_down_in, 0);
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
        foodPrice.setText("￥" + DoubleUtils.truncateZero(data.getPrice()));
        foodComment.setText(data.getComment());
        Helper.loadImage(Glide.with(context), data.getPictureUri(), foodImage);
    }

}
