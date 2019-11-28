package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Food;

public class FoodListAdapter extends CustomAdapter<Food> {
    private FoodClickedListener listener;
    public FoodListAdapter(FoodClickedListener listener){ this.listener = listener; }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder)holder).view.setOnClickListener(v -> {
            if (listener != null) listener.FoodClicked(holder, data.Get(position));
        });
    }

    class ViewHolder extends CustomViewHolder {
        private ImageView food_image;
        private TextView food_name;
        private TagsFragment fragment;

        ViewHolder(View view) {
            super(view);
            food_image = view.findViewById(R.id.food_image);
            food_name = view.findViewById(R.id.food_name);
            fragment = new TagsFragment();
        }

        @Override
        void setData(Food data) {
            food_image.setImageBitmap(Helper.GetFoodBitmap(data));
            food_name.setText(data.Name);
            fragment.SetData(data.GetTags(), false);
        }
    }
}
