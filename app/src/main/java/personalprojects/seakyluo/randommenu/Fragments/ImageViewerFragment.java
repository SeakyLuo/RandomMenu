package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import personalprojects.seakyluo.randommenu.adapters.impl.ImageAdapter;
import personalprojects.seakyluo.randommenu.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.R;

public class ImageViewerFragment extends Fragment {
    public static final String TAG = "ImageViewerFragment";
    private int current = -1;
    private ImageAdapter adapter = new ImageAdapter(ImageView.ScaleType.CENTER_CROP);
    private ViewPager viewPager;
    private ImageButton prev_image_button, next_image_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imageviewer, container, false);
        prev_image_button = view.findViewById(R.id.prev_image_button);
        next_image_button = view.findViewById(R.id.next_image_button);
        viewPager = view.findViewById(R.id.imageViewPager);

        prev_image_button.setOnClickListener(v -> scrollTo(current - 1));
        next_image_button.setOnClickListener(v -> scrollTo(current + 1));
        adapter.setOnDataChangedListener(images -> setButtonVisibility(current));
        adapter.setContext(getContext());
        adapter.setOnImageClickedListener(v -> {
            Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.IMAGE, adapter.GetData());
            intent.putExtra(FullScreenImageActivity.INDEX, current);
            startActivity(intent);
        });
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setButtonVisibility(current = i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        scrollTo(current);
        setButtonVisibility(current);
        return view;
    }
    public void scrollTo(int index) { viewPager.setCurrentItem(index, true); }
    public void setButtonVisibility(int current){
        prev_image_button.setVisibility(current <= 0 ? View.INVISIBLE : View.VISIBLE);
        next_image_button.setVisibility(current < 0 || current == adapter.getCount() - 1 ? View.INVISIBLE : View.VISIBLE);
    }
    public void setImages(AList<String> images, String cover) {
        current = images.indexOf(cover);
        adapter.SetData(images);
        if (viewPager != null){
            scrollTo(current);
            setButtonVisibility(current);
        }
    }
    public int getCurrent() { return current; }
    public String getCurrentImage() { return adapter.Get(current); }
    public String setCurrentImage(String image) { return adapter.Set(image, current); }
    public int removeCurrentImage() {
        int index = adapter.Remove(current--);
        if (current != -1) scrollTo(current);
        return index;
    }
    public int removeImage(int index) {
        return adapter.Remove(index);
    }
    public void moveImage(int from, int to) { adapter.Move(from, to); }
    public AList<String> addImages(AList<String> data) {
        adapter.Add(data);
        scrollTo(current = 0);
        return data;
    }
    public String addImage(String data) {
        adapter.Add(data);
        scrollTo(current = 0);
        return data;
    }
    public void Move(int from, int to) {
        adapter.Move(from, to);
        if (current == from){
            current = to;
            scrollTo(to);
        }
    }
}
