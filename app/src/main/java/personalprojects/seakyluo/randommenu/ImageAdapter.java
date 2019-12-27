package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;

public class ImageAdapter extends PagerAdapter {
    private Context context;
    private AList<String> images;
    private ImageView.ScaleType scaleType;
    private View.OnClickListener clickListener;

    public ImageAdapter(Context context, AList<String> images, ImageView.ScaleType scaleType) {
        this(images, scaleType);
        this.context = context;
    }
    public ImageAdapter(AList<String> images, ImageView.ScaleType scaleType){
        this.images = images;
        this.scaleType = scaleType;
    }
    public void setContext(Context context) { this.context = context; }
    public String Add(String image) {
        images.Add(image, 0);
        notifyDataSetChanged();
        return image;
    }
    public AList<String> Add(AList<String> images) {
        this.images.AddAll(images, 0);
        notifyDataSetChanged();
        return images;
    }
    public boolean Remove(String image) {
        boolean result = images.Remove(image);
        notifyDataSetChanged();
        return result;
    }
    public String Set(String image, int index){
        images.Set(image, index);
        notifyDataSetChanged();
        return image;
    }
    public void SetData(AList<String> images){
        this.images.CopyFrom(images);
        notifyDataSetChanged();
    }
    public void setOnImageClickedListener(View.OnClickListener listener) { clickListener = listener; }

    @Override
    public int getCount() { return images.Count(); }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView;
        if (scaleType == ImageView.ScaleType.CENTER_INSIDE)
            imageView = new ScalableImageView(context);
        else
            imageView = new ImageView(context);
        imageView.setScaleType(scaleType);
        imageView.setOnClickListener(clickListener);
        Helper.LoadImage(Glide.with(context), images.Get(position), imageView);
        container.addView(imageView, 0);
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}