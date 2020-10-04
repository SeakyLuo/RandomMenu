package personalprojects.seakyluo.randommenu.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_food_thumbnail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        Food food = data.get(position);
        viewHolder.view.setOnClickListener(v -> {
            if (foodClickedListener != null) foodClickedListener.click(holder, food);
        });
        viewHolder.view.setOnLongClickListener(v -> {
            boolean hasListener = longClickListener != null;
           if (hasListener) longClickListener.click(holder, food);
           return hasListener;
        });
    }

    public void SetFoodLiked(Food food) { ((ViewHolder)viewHolders.first(vh -> vh.data.equals(food))).SetLiked(food.IsFavorite()); }

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

    class ViewHolder extends CustomViewHolder {
        private ImageView food_image;
        private TextView food_name;
        private View liked;
        ViewHolder(View view) {
            super(view);
            food_name = view.findViewById(R.id.food_name);
            food_image = view.findViewById(R.id.food_image);
            liked = view.findViewById(R.id.liked_image);
        }

        @Override
        void setData(Food data) {
            food_name.setText(data.Name);
            Helper.loadImage(Glide.with(view), data.getCover(), food_image);
            SetLiked(data.IsFavorite());
        }

        void SetLiked(boolean isFavorite) { liked.setVisibility(isFavorite ? View.VISIBLE : View.GONE); }
    }
}
