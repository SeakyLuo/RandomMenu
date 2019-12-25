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
    public static Bitmap image;
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

//        RelativeLayout background = findViewById(R.id.fullscreen_background);
//        background.setOnClickListener(v -> finish());
        ImageView fullscreen_image = findViewById(R.id.fullscreen_image);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor * detector.getScaleFactor(), 10.0f));
                fullscreen_image.setScaleX(mScaleFactor);
                fullscreen_image.setScaleY(mScaleFactor);
                return true;
            }
        });

//        ViewPager viewPager = findViewById(R.id.imageViewPager);
//        ImageAdapter adapter = new ImageAdapter(this, new AList<>(getIntent().getStringExtra(IMAGE)));
//        viewPager.setAdapter(adapter); // Here we are passing and setting the adapter for the images

        if (image == null) Helper.LoadImage(Glide.with(this), getIntent().getStringArrayListExtra(IMAGE).get(0), fullscreen_image);
        else fullscreen_image.setImageBitmap(image);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return mScaleGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public void finish() {
        super.finish();
        image = null;
    }
}
