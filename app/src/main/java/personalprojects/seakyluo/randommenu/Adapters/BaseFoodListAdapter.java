package personalprojects.seakyluo.randommenu.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.R;

public abstract class BaseFoodListAdapter extends CustomAdapter<Food> {
    protected DataItemClickedListener<Food> foodClickedListener;
    protected boolean showLikeImage = true;
    public void setFoodClickedListener(DataItemClickedListener<Food> foodClickedListener) { this.foodClickedListener = foodClickedListener; }
    public void setShowLikeImage(boolean showLikeImage) { this.showLikeImage = showLikeImage; }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<Food>.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.view.setOnClickListener(v -> {
            if (foodClickedListener != null){
                foodClickedListener.click(holder, holder.data);
            }
        });
    }

    public class BaseViewHolder extends CustomViewHolder {
        protected ImageView food_image, liked_image;
        protected TextView food_name;

        public BaseViewHolder(@NonNull View view) {
            super(view);
            food_image = view.findViewById(R.id.food_image);
            food_name = view.findViewById(R.id.food_name);
            liked_image = view.findViewById(R.id.liked_image);
            food_image.setOnClickListener(v -> {
                if (data.HasImage()){
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra(FullScreenImageActivity.IMAGE, data.Images.toArrayList());
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, R.string.no_food_image, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        void setData(Food data) {
            Helper.loadImage(Glide.with(view), data.getCover(), food_image);
            food_name.setText(data.Name);
            liked_image.setVisibility(showLikeImage && data.IsFavorite() ? View.VISIBLE : View.GONE);
        }
    }
}
