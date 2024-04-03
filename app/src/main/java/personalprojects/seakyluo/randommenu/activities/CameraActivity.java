package personalprojects.seakyluo.randommenu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.FragmentManager;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.fragments.ImageViewerFragment;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.preview_view);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            Intent intent = getIntent();
        } else {

        }

        ImageUtils.openCameraX(this, previewView);
    }
}
