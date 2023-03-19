package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class FoodAdapter extends CustomAdapter<SelfFood> {
    @Setter
    private DataItemClickedListener<SelfFood> foodClickedListener, foodLongClickListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_food_thumbnail;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, SelfFood data, int position) {
        View view = viewHolder.getView();
        TextView food_name = view.findViewById(R.id.food_name);
        ImageView foodImage = view.findViewById(R.id.food_image);

        food_name.setText(data.getName());
        ImageUtils.loadImage(view, data.getCover(), foodImage);
        setFoodLiked(view, data.isFavorite());
        view.setOnClickListener(v -> {
            if (foodClickedListener != null) foodClickedListener.click(viewHolder, data);
        });
        view.setOnLongClickListener(v -> {
            boolean hasListener = foodLongClickListener != null;
            if (hasListener) foodLongClickListener.click(viewHolder, data);
            return hasListener;
        });
    }

    public void updateFood(SelfFood food){
        int index = data.indexOf(f -> f.getId() == food.getId());
        if (index != -1){
            set(food, index);
        }
    }

    public void setFoodLiked(SelfFood food) {
        CustomViewHolder viewHolder = viewHolders.first(vh -> vh.getData().equals(food));
        setFoodLiked(viewHolder.getView(), food.isFavorite());
    }

    private void setFoodLiked(View view, boolean isFavorite){
        view.findViewById(R.id.liked_image).setVisibility(isFavorite ? View.VISIBLE : View.GONE);
    }

}
