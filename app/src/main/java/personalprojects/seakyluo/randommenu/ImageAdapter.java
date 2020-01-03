package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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
    private OnDataChangedListener<AList<String>> dataChangedListener;
//    private ViewGroup container;

    public ImageAdapter(Context context, AList<String> images, ImageView.ScaleType scaleType) {
        this.context = context;
        this.images = images;
        this.scaleType = scaleType;
    }
    public ImageAdapter(ImageView.ScaleType scaleType){
        this.images = new AList<>();
        this.scaleType = scaleType;
    }
    public void setContext(Context context) { this.context = context; }
    public void setOnDataChangedListener(OnDataChangedListener<AList<String>> listener) { dataChangedListener = listener; }
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
    public int Remove(int index) {
        images.Pop(index);
        notifyDataSetChanged();
        return index;
    }
    public String Set(String image, int index){
        images.Set(image, index);
        notifyDataSetChanged();
        return image;
    }
    public AList<String> SetData(AList<String> images){
        this.images.CopyFrom(images);
//        if (container != null){
//            int count = container.getChildCount();
//            for (int i = 0; i < count; i++)
//                Helper.LoadImage(Glide.with(context), images.Get(i), (ImageView) container.getChildAt(i));
//        }
        notifyDataSetChanged();
        return this.images;
    }
    public AList<String> GetData() { return images; }
    public String Get(int index) { return images.Get(index); }
    public void setOnImageClickedListener(View.OnClickListener listener) { clickListener = listener; }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (dataChangedListener != null)
            dataChangedListener.OnChange(images);
    }

    @Override
    public int getItemPosition(Object object) { return POSITION_NONE; }

    @Override
    public int getCount() { return images.Count(); }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        this.container = container;
        ImageView imageView;
        if (scaleType == ImageView.ScaleType.CENTER_INSIDE)
            imageView = new ScalableImageView(context);
        else
            imageView = new ImageView(context);
        imageView.setScaleType(scaleType);
        imageView.setOnClickListener(clickListener);
        Helper.LoadImage(Glide.with(context), images.Get(position), imageView);
        container.addView(imageView);
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}

interface OnDataChangedListener<T>{
    void OnChange(T newData);
}