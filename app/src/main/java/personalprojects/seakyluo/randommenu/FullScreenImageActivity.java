package personalprojects.seakyluo.randommenu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class FullScreenImageActivity extends AppCompatActivity {
    public static Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ConstraintLayout fullscreen_background = findViewById(R.id.fullscreen_background);
        fullscreen_background.setOnClickListener(v -> finish());
        ImageView fullscreen_image = findViewById(R.id.fullscreen_image);
        if (image != null) fullscreen_image.setImageBitmap(image);
    }
}
