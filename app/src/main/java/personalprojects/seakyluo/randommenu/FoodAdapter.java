package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class FoodAdapter extends CustomAdapter<Food> {
    private OnDataItemClickedListener<Food> foodClickedListener, longClickListener;
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
        Food food = data.Get(position);
        viewHolder.view.setOnClickListener(v -> {
            if (foodClickedListener != null) foodClickedListener.Click(holder, food);
        });
        viewHolder.view.setOnLongClickListener(v -> {
            boolean hasListener = longClickListener != null;
           if (hasListener) longClickListener.Click(holder, food);
           return hasListener;
        });
    }

    public void SetFoodLiked(Food food) { ((ViewHolder)viewHolders.First(vh -> vh.data.equals(food))).SetLiked(food.IsFavorite()); }

    @Override
    public void SetData(AList<Food> data){
        all.CopyFrom(data);
        super.SetData(data);
    }

    public void Reset() {
        data.CopyFrom(all);
        notifyDataSetChanged();
    }

    public void Filter(Tag tag){
        data.CopyFrom(all.Find(f -> f.HasTag(tag)));
        notifyDataSetChanged();
    }

    public void SetOnFoodClickedListener(OnDataItemClickedListener<Food> listener){ foodClickedListener = listener; }
    public void SetOnFoodLongClickListener(OnDataItemClickedListener<Food> listener){ longClickListener = listener; }

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
        void SetData(Food data) {
            food_name.setText(data.Name);
            Helper.LoadImage(Glide.with(view), data.GetCover(), food_image);
            SetLiked(data.IsFavorite());
        }

        void SetLiked(boolean isFavorite) { liked.setVisibility(isFavorite ? View.VISIBLE : View.GONE); }
    }
}
