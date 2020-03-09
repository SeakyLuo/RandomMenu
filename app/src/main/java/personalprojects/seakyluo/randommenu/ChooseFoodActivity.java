package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class ChooseFoodActivity extends AppCompatActivity {
    public static final int CODE = 12;
    private FoodListFragment foodListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_food);

        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.confirm_button).setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, TagsFragment.TAG, foodListFragment);
    }
}
