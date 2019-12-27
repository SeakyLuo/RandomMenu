package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import personalprojects.seakyluo.randommenu.Models.AList;

public class ImageViewerFragment extends Fragment {
    public static final String TAG = "ImageViewerFragment";
    private int current = 0;
    private AList<String> images = new AList<>();
    public ImageAdapter adapter = new ImageAdapter(images, ImageView.ScaleType.CENTER_CROP);
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imageviewer, container, false);
        viewPager = view.findViewById(R.id.imageViewPager);
        adapter.setContext(getContext());
        adapter.setOnImageClickedListener(v -> {
            Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.IMAGE, images.ToArrayList());
            intent.putExtra(FullScreenImageActivity.INDEX, current);
            startActivity(intent);
        });
        viewPager.setAdapter(adapter); // Here we are passing and setting the adapter for the images
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                current = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        return view;
    }

    public void setImages(AList<String> images) { adapter.SetData(images); }
    public int getCurrent() { return current; }
    public String setCurrentImage(String image) { return adapter.Set(image, current); }
    public int removeCurrentImage() {
        int index = current;
        adapter.Remove(current--);
        return index;
    }
}
