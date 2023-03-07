package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.dialogs.RestaurantFoodDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class RestaurantFoodAdapter extends DraggableAdapter<RestaurantFoodVO> {

    @Setter
    private DataItemClickedListener<RestaurantFoodVO> onFoodClickListener;

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
        fillFood(view, data);
        view.setOnClickListener(v -> onFoodClickListener.click(viewHolder, data));
    }

    private void fillFood(View view, RestaurantFoodVO data){
        TextView foodName = view.findViewById(R.id.food_name);
        TextView foodNote = view.findViewById(R.id.food_note);
        ImageView foodImage = view.findViewById(R.id.food_image);
        ImageView defaultFoodImage = view.findViewById(R.id.default_food_image);

        foodName.setText(data.getName());
        String comment = data.getComment();
        if (StringUtils.isEmpty(comment)){
            if (data.getPrice() == 0){
                foodNote.setVisibility(View.GONE);
            } else {
                foodNote.setVisibility(View.VISIBLE);
                foodNote.setText("￥" + DoubleUtils.truncateZero(data.getPrice()));
            }
        } else {
            foodNote.setVisibility(View.VISIBLE);
            foodNote.setText("“" + comment + "”");
        }
        String pictureUri = data.getPictureUri();
        if (StringUtils.isEmpty(pictureUri)){
            foodImage.setVisibility(View.GONE);
            defaultFoodImage.setVisibility(View.VISIBLE);
        } else {
            foodImage.setVisibility(View.VISIBLE);
            defaultFoodImage.setVisibility(View.GONE);
            ImageUtils.loadImage(context, pictureUri, foodImage);
        }
    }

}
