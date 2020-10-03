package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.TagsFragment;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class ChooseFoodActivity extends AppCompatActivity {
    public static final int CODE = 12;
    public static final String TAG = "ChooseFoodActivity";
    private ImageButton clear_button;
    private AutoCompleteTextView inputBox;
    private TagsFragment tagsFragment;
    private FoodListFragment foodListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_food);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment())
                                                          .add(R.id.food_list_frame, foodListFragment = new FoodListFragment()).commit();
            foodListFragment.setSelectable(true);
        }else{
            tagsFragment = (TagsFragment) getSupportFragmentManager().getFragment(savedInstanceState, TagsFragment.TAG);
            foodListFragment = (FoodListFragment) getSupportFragmentManager().getFragment(savedInstanceState, FoodListFragment.TAG);
        }
        clear_button = findViewById(R.id.clear_button);
        inputBox = findViewById(R.id.input_box);
        AList<Food> foods = new AList<>(getIntent().getParcelableArrayListExtra(TAG));

        tagsFragment.setSpanCount(1);
        tagsFragment.SetCloseable(true);
        tagsFragment.setData(foods.convert(f -> new Tag(f.Name)));
        tagsFragment.setTagClosedListener((vh, food) -> foodListFragment.unselectFood(food.Name));
        foodListFragment.setData(Settings.settings.Foods);
        foodListFragment.setSelectedFood(foods);
        foodListFragment.setFoodSelectedListener((vh, selected) -> {
            Tag food = new Tag(vh.data.Name);
            if (selected) tagsFragment.add(food);
            else tagsFragment.remove(food);
        });
        clear_button.setVisibility(View.GONE);
        clear_button.setOnClickListener(v -> {
            inputBox.setText("");
            clear_button.setVisibility(View.GONE);
        });
        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        findViewById(R.id.confirm_button).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(TAG, foodListFragment.getSelectedFood().toArrayList());
            setResult(RESULT_OK, intent);
            finish();
        });
        inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s);
            }
        });
        inputBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(inputBox.getText());
                return true;
            }
            return false;
        });
    }

    private void search(Editable s){
        String keyword = SearchActivity.getKeyword(s);
        if (keyword.isEmpty()){
            clear_button.setVisibility(View.GONE);
            foodListFragment.cancelFilter();
        }else{
            clear_button.setVisibility(View.VISIBLE);
            foodListFragment.filter(keyword);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, TagsFragment.TAG, tagsFragment);
        fragmentManager.putFragment(outState, FoodListFragment.TAG, foodListFragment);
    }
}
