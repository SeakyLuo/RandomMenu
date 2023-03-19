package personalprojects.seakyluo.randommenu.adapters;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.activities.impl.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public abstract class BaseFoodListAdapter extends CustomAdapter<SelfFood> {
    @Setter
    protected DataItemClickedListener<SelfFood> foodClickedListener;
    @Setter
    protected boolean showLikeImage = true;

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, SelfFood data, int position) {
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
                intent.putExtra(FullScreenImageActivity.IMAGE, new ArrayList<>(data.getImages()));
                context.startActivity(intent);
            }else{
                Toast.makeText(context, R.string.no_food_image, Toast.LENGTH_SHORT).show();
            }
        });

        ImageUtils.loadImage(view, data.getCover(), foodImage);
        foodName.setText(data.getName());
        likedImage.setVisibility(showLikeImage && data.isFavorite() ? View.VISIBLE : View.GONE);
    }

}
