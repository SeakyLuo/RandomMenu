package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class FoodListAdapter extends CustomAdapter<Food> {
    private FoodClickedListener foodClickedListener;
    private TagClickedListener tagClickedListener;
    public FoodListAdapter(FoodClickedListener foodClickedListener){ this.foodClickedListener = foodClickedListener; }
    public void SetTagClickedListener(TagClickedListener tagClickedListener) { this.tagClickedListener = tagClickedListener; }

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
            if (foodClickedListener != null) foodClickedListener.FoodClicked(holder, data.Get(position));
        });
        viewHolder.adapter.SetTagClickedListener(tagClickedListener);
    }



    class ViewHolder extends CustomViewHolder {
        private ImageView food_image;
        private TextView food_name;
        private RecyclerView recyclerView;
        private TagAdapter adapter;

        ViewHolder(View view) {
            super(view);
            food_image = view.findViewById(R.id.food_image);
            food_name = view.findViewById(R.id.food_name);
            recyclerView = view.findViewById(R.id.tags_recycler_view);
            adapter = new TagAdapter();

            food_image.setOnClickListener(v -> {
                FullScreenImageActivity.image = Helper.GetFoodBitmap(food_image);
                activity.startActivity(new Intent(activity, FullScreenImageActivity.class));
            });
            recyclerView.setAdapter(adapter);
        }

        @Override
        void setData(Food data) {
            food_image.setImageBitmap(Helper.GetFoodBitmap(data));
            food_name.setText(data.Name);
            adapter.setData(data.GetTags().Convert(t -> new ToggleTag(t, false)));
        }
    }
}
