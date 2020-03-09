package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class ChooseFoodActivity extends AppCompatActivity {
    public static final int CODE = 12;
    public static final String TAG = "ChooseFoodActivity";
    private TagsFragment tagsFragment;
    private FoodListFragment foodListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_food);

        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        findViewById(R.id.confirm_button).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(TAG, tagsFragment.GetData().Convert(t -> t.Name).ToArray());
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, TagsFragment.TAG, tagsFragment);
        fragmentManager.putFragment(outState, FoodListFragment.TAG, foodListFragment);
    }
}
