package personalprojects.seakyluo.randommenu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;

public class FullScreenImageActivity extends AppCompatActivity {
    public static final String IMAGE = "IMAGE";
    private AList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

//        RelativeLayout background = findViewById(R.id.fullscreen_background);
//        background.setOnClickListener(v -> finish());

        ViewPager viewPager = findViewById(R.id.imageViewPager);
        ImageAdapter adapter = new ImageAdapter(this, images = new AList<>(getIntent().getStringArrayListExtra(IMAGE)), ImageView.ScaleType.CENTER_INSIDE);
        viewPager.setAdapter(adapter); // Here we are passing and setting the adapter for the images
    }
}
