package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.controls.ScalableImageView;

public class ImageAdapter extends PagerAdapter {
    @Setter
    private Context context;
    @Getter
    private AList<String> data;
    private ImageView.ScaleType scaleType;
    @Setter
    private View.OnClickListener imageClickedListener;
    @Setter
    private Consumer<AList<String>> dataChangedListener;

    public ImageAdapter(Context context, AList<String> data, ImageView.ScaleType scaleType) {
        this.context = context;
        this.data = data;
        this.scaleType = scaleType;
    }
    public ImageAdapter(ImageView.ScaleType scaleType){
        this.data = new AList<>();
        this.scaleType = scaleType;
    }
    public String add(String image) {
        data.with(image, 0);
        notifyDataSetChanged();
        return image;
    }
    public AList<String> add(AList<String> images) {
        this.data.with(images, 0);
        notifyDataSetChanged();
        return images;
    }
    public int remove(int index) {
        data.pop(index);
        notifyDataSetChanged();
        return index;
    }
    public String set(String image, int index){
        data.set(image, index);
        notifyDataSetChanged();
        return image;
    }
    public int indexOf(String image) {
        return data.indexOf(image);
    }
    public AList<String> setData(List<String> images){
        this.data.copyFrom(images);
//        if (container != null){
//            int count = container.getChildCount();
//            for (int i = 0; i < count; i++)
//                ImageUtils.loadImage(Glide.with(context), images.Get(i), (ImageView) container.getChildAt(i));
//        }
        notifyDataSetChanged();
        return this.data;
    }
    public String get(int index) {
        return data.get(index);
    }

    public void move(int from, int to) {
        data.move(from, to);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return data.isEmpty();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (dataChangedListener != null)
            dataChangedListener.accept(data);
    }

    @Override
    public int getItemPosition(Object object) { return POSITION_NONE; }

    @Override
    public int getCount() { return data.size(); }

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
        imageView.setOnClickListener(imageClickedListener);
        ImageUtils.loadImage(context, data.get(position), imageView);
        container.addView(imageView);
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}