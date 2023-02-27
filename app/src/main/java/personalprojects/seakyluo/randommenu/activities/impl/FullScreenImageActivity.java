package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.ImageAdapter;
import personalprojects.seakyluo.randommenu.models.AList;

public class FullScreenImageActivity extends AppCompatActivity {
    public static final String IMAGE = "Image", INDEX = "Index";
    private AList<String> images;
    private TextView swipeCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        swipeCounter = findViewById(R.id.swipeCounter);
        Intent intent = getIntent();

        ViewPager viewPager = findViewById(R.id.imageViewPager);
        viewPager.setOnClickListener(v -> finish());
        ImageAdapter adapter = new ImageAdapter(this, images = new AList<>(intent.getStringArrayListExtra(IMAGE)), ImageView.ScaleType.CENTER_INSIDE);
        adapter.setOnImageClickedListener(v -> finish());
        swipeCounter.setVisibility(images.size() == 1 ? View.GONE : View.VISIBLE);
        setCounter(1);
        viewPager.setAdapter(adapter); // Here we are passing and setting the adapter for the images
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setCounter(i + 1);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(intent.getIntExtra(INDEX, 0));
    }

    private void setCounter(int current){
        swipeCounter.setText(current + "/" + images.size());
    }
}
