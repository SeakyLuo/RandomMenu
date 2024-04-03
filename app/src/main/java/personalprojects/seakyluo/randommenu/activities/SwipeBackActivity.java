package personalprojects.seakyluo.randommenu.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.jude.swipbackhelper.SwipeBackHelper;

import personalprojects.seakyluo.randommenu.R;

public class SwipeBackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackHelper.onCreate(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }
}
