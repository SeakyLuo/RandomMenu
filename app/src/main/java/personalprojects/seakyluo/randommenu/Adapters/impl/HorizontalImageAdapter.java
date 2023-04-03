package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class HorizontalImageAdapter extends DraggableAdapter<String> {

    private boolean editable = true;

    public HorizontalImageAdapter(Context context){
        this.context = context;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_image;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, String data, int position) {
        View view = viewHolder.getView();
        ImageView imageView = view.findViewById(R.id.image);
        ImageButton closeImage = view.findViewById(R.id.closeImageButton);

        ImageUtils.loadImage(context, data, imageView);
        imageView.setOnClickListener(v -> {
            FragmentActivity activity = getContextAsFragmentActivity();
            Intent intent = new Intent(activity, FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.IMAGE, getData());
            intent.putExtra(FullScreenImageActivity.INDEX, position);
            activity.startActivity(intent);
        });
        closeImage.setOnClickListener(v -> removeAt(viewHolder.getBindingAdapterPosition()));
        closeImage.setVisibility(editable ? View.VISIBLE : View.GONE);
    }

    public void setEditable(boolean editable){
        this.editable = editable;
        for (CustomViewHolder viewHolder : viewHolders){
            viewHolder.getView().findViewById(R.id.closeImageButton).setVisibility(editable ? View.VISIBLE : View.GONE);
        }
    }
}
