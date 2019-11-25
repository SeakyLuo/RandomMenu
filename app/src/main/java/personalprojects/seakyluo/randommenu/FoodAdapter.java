package personalprojects.seakyluo.randommenu;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class FoodAdapter extends CustomAdapter<Food> {
    private FoodClickedListener listener;
    private AList<Food> all;
    public FoodAdapter(ArrayList<Food> data){
        all = new AList<>(data);
        setData(data);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_food_thumbnail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            if (listener != null) listener.FoodClicked(viewHolder, data.Get(position));
        });
    }

    public void Reset() {
        data.CopyFrom(all);
        notifyDataSetChanged();
    }

    public void Filter(ToggleTag tag){
        data.CopyFrom(all.Filter(f -> f.HasTag(tag)));
        notifyDataSetChanged();
    }

    public void SetOnFoodClickedListener(FoodClickedListener listener){ this.listener = listener; }

    class ViewHolder extends CustomViewHolder {
        private ImageView food_image;
        private TextView food_name;
        ViewHolder(View view) {
            super(view);
            food_name = view.findViewById(R.id.food_name);
            food_image = view.findViewById(R.id.food_image);
        }

        @Override
        void setData(Food data) {
            food_name.setText(data.Name);
            food_image.setImageBitmap(Helper.GetFoodBitmap(data.ImagePath));
        }
    }
}

interface FoodClickedListener{
    void FoodClicked(FoodAdapter.ViewHolder viewHolder, Food food);
}
