package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;

public class ImageViewerFragment extends Fragment {
    public static final String TAG = "ImageViewerFragment";
    private int current = -1;
    public ImageAdapter adapter = new ImageAdapter(ImageView.ScaleType.CENTER_CROP);
    private ViewPager viewPager;
    private ImageButton prev_image_button, next_image_button;
    private int coverIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imageviewer, container, false);
        prev_image_button = view.findViewById(R.id.prev_image_button);
        next_image_button = view.findViewById(R.id.next_image_button);
        viewPager = view.findViewById(R.id.imageViewPager);

        prev_image_button.setOnClickListener(v -> scrollTo(current - 1));
        next_image_button.setOnClickListener(v -> scrollTo(current + 1));
        setButtonVisibility(current);
        adapter.setOnDataChangedListener(images -> {
            if (images.Count() == 0) current = -1;
            else scrollTo(current = 0);
            setButtonVisibility(current);
        });
        adapter.setContext(getContext());
        adapter.setOnImageClickedListener(v -> {
            Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.IMAGE, adapter.GetData().ToArrayList());
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
                setButtonVisibility(current = i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        if (coverIndex != -1) scrollTo(coverIndex);
        return view;
    }
    public void scrollTo(int index) { viewPager.setCurrentItem(index, true); }
    public void setButtonVisibility(int current){
        prev_image_button.setVisibility(current <= 0 ? View.INVISIBLE : View.VISIBLE);
        next_image_button.setVisibility(current < 0 || current == adapter.getCount() - 1 ? View.INVISIBLE : View.VISIBLE);
    }
    public void setCover(String image){
        coverIndex = adapter.IndexOf(image);
        if (viewPager != null) scrollTo(adapter.IndexOf(image));
    }
    public void setImages(AList<String> images) { images.CopyFrom(adapter.SetData(images)); }
    public int getCurrent() { return current; }
    public String getCurrentImage() { return adapter.Get(current); }
    public String setCurrentImage(String image) { return adapter.Set(image, current); }
    public int removeCurrentImage() { return adapter.Remove(current--); }
//    public void Move(int from, int to) {
//        adapter.Move(from, to);
//        if (current == from) current = to;
//    }
}
