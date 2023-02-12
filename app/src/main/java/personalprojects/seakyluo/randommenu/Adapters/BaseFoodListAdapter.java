package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
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
    protected void fillViewHolder(CustomViewHolder viewHolder, Food data, int position) {
        View view = viewHolder.getView();
        TextView foodName = view.findViewById(R.id.food_name);
        ImageView foodImage = view.findViewById(R.id.food_image);
        ImageView likedImage = view.findViewById(R.id.liked_image);

        view.setOnClickListener(v -> {
            if (foodClickedListener != null){
                foodClickedListener.click(viewHolder, data);
            }
        });

        foodName.setOnClickListener(v -> {
            if (data.hasImage()){
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.IMAGE, data.Images);
                context.startActivity(intent);
            }else{
                Toast.makeText(context, R.string.no_food_image, Toast.LENGTH_SHORT).show();
            }
        });

        Helper.loadImage(Glide.with(view), data.getCover(), foodImage);
        foodName.setText(data.Name);
        likedImage.setVisibility(showLikeImage && data.isFavorite() ? View.VISIBLE : View.GONE);
    }


    public void setContext(Context context){
        this.context = context;
    }

}
