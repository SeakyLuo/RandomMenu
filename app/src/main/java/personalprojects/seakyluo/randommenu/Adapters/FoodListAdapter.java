package personalprojects.seakyluo.randommenu.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class FoodListAdapter extends CustomAdapter<Food> {
    public AList<Food> selectedFood = new AList<>();
    private boolean selectable = false;
    private OnDataItemClickedListener<Food> foodClickedListener;
    private OnDataItemClickedListener<Tag> tagClickedListener;
    private OnDataItemClickedListener<Boolean> selectionChangedListener;
    private boolean showLikeImage = true;
//    public FoodListAdapter(Context context) { this.context = context; }
    public void setFoodClickedListener(OnDataItemClickedListener<Food> foodClickedListener) { this.foodClickedListener = foodClickedListener; }
    public void setTagClickedListener(OnDataItemClickedListener<Tag> tagClickedListener) { this.tagClickedListener = tagClickedListener; }
    public void setsSelectionChangedListener(OnDataItemClickedListener<Boolean> selectionChangedListener) { this.selectionChangedListener = selectionChangedListener; }
    public void setShowLikeImage(boolean showLikeImage) { this.showLikeImage = showLikeImage; }
    public void setSelectable(boolean selectable) { this.selectable = selectable; }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<Food>.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            if (foodClickedListener != null){
                foodClickedListener.click(holder, viewHolder.data);
            }
            if (selectable){
                viewHolder.setSelected(!viewHolder.selected);
                if (selectionChangedListener != null){
                    selectionChangedListener.click(holder, viewHolder.selected);
                }
            }
        });
        viewHolder.adapter.SetTagClickedListener((v, t) -> {
            if (tagClickedListener != null) tagClickedListener.click(v, t);
        });
    }

    public void setSelectedFood(AList<Food> foods){
        selectedFood.copyFrom(foods);
    }

    public class ViewHolder extends CustomViewHolder {
        private ImageView food_image, liked_image, checked_image;
        private TextView food_name;
        private TagAdapter adapter = new TagAdapter();
        private boolean selected = false;

        public ViewHolder(View view) {
            super(view);
            food_image = view.findViewById(R.id.food_image);
            food_name = view.findViewById(R.id.food_name);
            liked_image = view.findViewById(R.id.liked_image);
            checked_image = view.findViewById(R.id.checked_image);
            RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);

            food_image.setOnClickListener(v -> {
                if (data.HasImage()){
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra(FullScreenImageActivity.IMAGE, data.Images.toArrayList());
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, R.string.no_food_image, Toast.LENGTH_SHORT).show();
                }
            });
            recyclerView.setAdapter(adapter);
        }

        public void setSelected(boolean selected){
            checked_image.setVisibility((this.selected = selected) ? View.VISIBLE : View.GONE);
            if (selected) selectedFood.add(data);
            else selectedFood.remove(data);
        }

        @Override
        public void setData(Food data) {
            Helper.LoadImage(Glide.with(view), data.GetCover(), food_image);
            food_name.setText(data.Name);
            liked_image.setVisibility(showLikeImage && data.IsFavorite() ? View.VISIBLE : View.GONE);
            adapter.setData(data.GetTags());
            checked_image.setVisibility(selectedFood.contains(data) ?  View.VISIBLE : View.GONE);
        }
    }
}
