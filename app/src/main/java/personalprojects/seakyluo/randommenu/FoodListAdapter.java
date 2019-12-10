package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class FoodListAdapter extends CustomAdapter<Food> {
    private OnDataItemClickedListener<Food> foodClickedListener;
    private OnDataItemClickedListener<Tag> tagClickedListener;
    public void SetFoodClickedListener(OnDataItemClickedListener<Food> foodClickedListener) { this.foodClickedListener = foodClickedListener; }
    public void SetTagClickedListener(OnDataItemClickedListener<Tag> tagClickedListener) { this.tagClickedListener = tagClickedListener; }

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

    class ViewHolder extends CustomViewHolder {
        private ImageView food_image;
        private TextView food_name;
        private TagAdapter adapter = new TagAdapter();

        ViewHolder(View view) {
            super(view);
            food_image = view.findViewById(R.id.food_image);
            food_name = view.findViewById(R.id.food_name);
            RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);

            food_image.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.IMAGE, data.ImagePath);
                context.startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }

        @Override
        void SetData(Food data) {
            Helper.LoadImage(Glide.with(view), data.ImagePath, food_image);
            food_name.setText(data.Name);
            adapter.SetData(data.GetTags());
        }
    }
}
