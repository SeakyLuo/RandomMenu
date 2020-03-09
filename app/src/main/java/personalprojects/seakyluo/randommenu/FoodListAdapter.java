package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class FoodListAdapter extends CustomAdapter<Food> {
    private boolean selectable = false;
    private OnDataItemClickedListener<Food> foodClickedListener;
    private OnDataItemClickedListener<Tag> tagClickedListener;
    private OnDataItemClickedListener<Boolean> selectionChangedListener;
    private boolean showLikeImage = true;
//    public FoodListAdapter(Context context) { this.context = context; }
    public void SetFoodClickedListener(OnDataItemClickedListener<Food> foodClickedListener) { this.foodClickedListener = foodClickedListener; }
    public void SetTagClickedListener(OnDataItemClickedListener<Tag> tagClickedListener) { this.tagClickedListener = tagClickedListener; }
    public void SetsSelectionChangedListener(OnDataItemClickedListener<Boolean> selectionChangedListener) { this.selectionChangedListener = selectionChangedListener; }
    public void SetShowLikeImage(boolean showLikeImage) { this.showLikeImage = showLikeImage; }
    public void SetSelectable(boolean selectable) { this.selectable = selectable; }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            if (foodClickedListener != null) foodClickedListener.Click(holder, data.Get(position));
        });
        viewHolder.adapter.SetTagClickedListener((v, t) -> {
            if (tagClickedListener != null) tagClickedListener.Click(v, t);
        });
    }

    public void SetSelectedFood(AList<Food> foods){
        viewHolders.ForEach(vh -> {
            if (foods.Contains(vh.data))
                ((ViewHolder)vh).setSelected(true);
        });
    }

    class ViewHolder extends CustomViewHolder {
        private ImageView food_image, liked_image, checked_image;
        private TextView food_name;
        private TagAdapter adapter = new TagAdapter();

        ViewHolder(View view) {
            super(view);
            food_image = view.findViewById(R.id.food_image);
            food_name = view.findViewById(R.id.food_name);
            liked_image = view.findViewById(R.id.liked_image);
            checked_image = view.findViewById(R.id.checked_image);
            RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);

            view.setOnClickListener(v -> {
                Log.d("fuck", data.Name);
                Log.d("fuck", String.valueOf(checked_image.getVisibility() == View.VISIBLE));
                if (selectionChangedListener != null)
                    selectionChangedListener.Click(this, checked_image.getVisibility() == View.VISIBLE);
            });
            food_image.setOnClickListener(v -> {
                if (data.HasImage()){
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra(FullScreenImageActivity.IMAGE, data.Images.ToArrayList());
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, R.string.no_food_image, Toast.LENGTH_SHORT).show();
                }
            });
            setSelected(selectable);
            recyclerView.setAdapter(adapter);
        }

        void setSelected(boolean selected){
            checked_image.setVisibility(selected ? View.GONE : View.VISIBLE);
        }

        @Override
        void SetData(Food data) {
            Helper.LoadImage(Glide.with(view), data.GetCover(), food_image);
            food_name.setText(data.Name);
            liked_image.setVisibility(showLikeImage && data.IsFavorite() ? View.VISIBLE : View.GONE);
            adapter.SetData(data.GetTags());
        }
    }
}
