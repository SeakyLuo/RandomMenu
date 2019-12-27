package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;

public class ImageAdapter extends PagerAdapter {
    private Context context;
    private AList<String> images;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
//    private float mScaleFactor = 1.0f;
//    private ScaleGestureDetector mScaleGestureDetector;

    public ImageAdapter(Context context, AList<String> images) {
        this.context = context;
        this.images = images;
    }
    public ImageAdapter(Context context, AList<String> images, ImageView.ScaleType scaleType) {
        this.context = context;
        this.images = images;
        this.scaleType = scaleType;
    }

    @Override
    public int getCount() { return images.Count(); }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScalableImageView imageView = new ScalableImageView(context);
        imageView.setScaleType(scaleType);
        Helper.LoadImage(Glide.with(context), images.Get(position), imageView);
        container.addView(imageView, 0);
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
