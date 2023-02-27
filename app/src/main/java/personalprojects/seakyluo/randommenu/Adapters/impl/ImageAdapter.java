package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import personalprojects.seakyluo.randommenu.interfaces.OnDataChangedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.controls.ScalableImageView;

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
    public String add(String image) {
        images.with(image, 0);
        notifyDataSetChanged();
        return image;
    }
    public AList<String> add(AList<String> images) {
        this.images.with(images, 0);
        notifyDataSetChanged();
        return images;
    }
    public int remove(int index) {
        images.pop(index);
        notifyDataSetChanged();
        return index;
    }
    public String set(String image, int index){
        images.set(image, index);
        notifyDataSetChanged();
        return image;
    }
    public int indexOf(String image) { return images.indexOf(image); }
    public AList<String> setData(AList<String> images){
        this.images.copyFrom(images);
//        if (container != null){
//            int count = container.getChildCount();
//            for (int i = 0; i < count; i++)
//                ImageUtils.loadImage(Glide.with(context), images.Get(i), (ImageView) container.getChildAt(i));
//        }
        notifyDataSetChanged();
        return this.images;
    }
    public AList<String> getData() { return images; }
    public String get(int index) { return images.get(index); }
    public void move(int from, int to) {
        images.move(from, to);
        notifyDataSetChanged();
    }
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
    public int getCount() { return images.size(); }

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
        ImageUtils.loadImage(context, images.get(position), imageView);
        container.addView(imageView);
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}