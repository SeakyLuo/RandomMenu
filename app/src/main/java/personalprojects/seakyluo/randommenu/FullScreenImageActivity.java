package personalprojects.seakyluo.randommenu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;

public class FullScreenImageActivity extends AppCompatActivity {
    public static final String IMAGE = "IMAGE";
    public static Bitmap image;
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ConstraintLayout fullscreen_background = findViewById(R.id.fullscreen_background);
        fullscreen_background.setOnClickListener(v -> finish());
        ImageView fullscreen_image = findViewById(R.id.fullscreen_image);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.d("fuck", "onScaleEnd");
            }
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.d("fuck", "onScaleBegin");
                return true;
            }
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.d("fuck", "onScale");
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor * detector.getScaleFactor(), 10.0f));
                fullscreen_image.setScaleX(mScaleFactor);
                fullscreen_image.setScaleY(mScaleFactor);
                return true;
            }
        });

        if (image == null) Helper.LoadImage(Glide.with(this), getIntent().getStringExtra(IMAGE), fullscreen_image);
        else fullscreen_image.setImageBitmap(image);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d("fuck", "onTouchEvent");
        return mScaleGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public void finish() {
        super.finish();
        image = null;
    }
}
