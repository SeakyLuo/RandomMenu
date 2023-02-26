package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.dialogs.RestaurantFoodDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class RestaurantFoodAdapter extends DraggableAdapter<RestaurantFoodVO> {

    public RestaurantFoodAdapter(Context context){
        this.context = context;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_restaurant_food;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, RestaurantFoodVO data, int position) {
        View view = viewHolder.getView();
        TextView foodName = view.findViewById(R.id.food_name);
        TextView foodNote = view.findViewById(R.id.food_note);
        ImageView foodImage = view.findViewById(R.id.food_image);

        fillFood(data, foodName, foodNote, foodImage);
        view.setOnClickListener(v -> {
//            RestaurantFoodDialog dialog = new RestaurantFoodDialog();
//            dialog.setFood(data);
//            dialog.setConfirmListener(food -> {
//                data.copyFrom(food);
//                fillFood(data, foodName, foodNote, foodImage);
//                if (clickedListener != null){
//                    clickedListener.click(viewHolder, data);
//                }
//            });
//            dialog.showNow(((FragmentActivity)context).getSupportFragmentManager(), RestaurantFoodDialog.TAG);
        });
    }

    private void fillFood(RestaurantFoodVO data, TextView foodName, TextView foodNote, ImageView foodImage){
        foodName.setText(data.getName());
        foodNote.setText("￥" + DoubleUtils.truncateZero(data.getPrice()));
        Helper.loadImage(Glide.with(context), data.getPictureUri(), foodImage);
    }

}
