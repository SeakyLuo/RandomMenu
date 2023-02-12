package personalprojects.seakyluo.randommenu.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class FoodAdapter extends CustomAdapter<Food> {
    private DataItemClickedListener<Food> foodClickedListener, longClickListener;
    private AList<Food> all = new AList<>();

    @Override
    public int getLayout(int viewType) {
        return R.layout.view_food_thumbnail;
    }

    @Override
    public void fillViewHolder(CustomViewHolder viewHolder, Food data, int position) {
        View view = viewHolder.getView();
        TextView food_name = view.findViewById(R.id.food_name);
        ImageView food_image = view.findViewById(R.id.food_image);

        food_name.setText(data.Name);
        Helper.loadImage(Glide.with(view), data.getCover(), food_image);
        setLiked(view, data.isFavorite());
        view.setOnClickListener(v -> {
            if (foodClickedListener != null) foodClickedListener.click(viewHolder, data);
        });
        view.setOnLongClickListener(v -> {
            boolean hasListener = longClickListener != null;
            if (hasListener) longClickListener.click(viewHolder, data);
            return hasListener;
        });
    }

    public void setFoodLiked(Food food) {
        CustomViewHolder viewHolder = viewHolders.first(vh -> vh.getData().equals(food));
        setLiked(viewHolder.getView(), food.isFavorite());
    }

    private void setLiked(View view, boolean isFavorite){
        view.findViewById(R.id.liked_image).setVisibility(isFavorite ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setData(AList<Food> data){
        all.copyFrom(data);
        super.setData(data);
    }

    public void Reset() {
        data.copyFrom(all);
        notifyDataSetChanged();
    }

    public void Filter(Tag tag){
        data.copyFrom(all.find(f -> f.hasTag(tag)));
        notifyDataSetChanged();
    }

    public void SetOnFoodClickedListener(DataItemClickedListener<Food> listener){ foodClickedListener = listener; }
    public void SetOnFoodLongClickListener(DataItemClickedListener<Food> listener){ longClickListener = listener; }

}
