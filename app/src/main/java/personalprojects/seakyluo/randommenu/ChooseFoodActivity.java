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

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

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
            foodListFragment.SetSelectable(true);
        }else{
            tagsFragment = (TagsFragment) getSupportFragmentManager().getFragment(savedInstanceState, TagsFragment.TAG);
            foodListFragment = (FoodListFragment) getSupportFragmentManager().getFragment(savedInstanceState, FoodListFragment.TAG);
        }
        clear_button = findViewById(R.id.clear_button);
        inputBox = findViewById(R.id.input_box);
        AList<Food> foods = new AList<>(getIntent().getParcelableArrayListExtra(TAG));

        tagsFragment.SetSpanCount(1);
        tagsFragment.SetCloseable(true);
        tagsFragment.SetData(foods.Convert(f -> new Tag(f.Name)));
        tagsFragment.SetTagClosedListener((vh, food) -> foodListFragment.UnselectFood(food.Name));
        foodListFragment.SetData(Settings.settings.Foods);
        foodListFragment.SetSelectedFood(foods);
        foodListFragment.SetFoodSelectedListener((vh, selected) -> {
            Tag food = new Tag(((FoodListAdapter.ViewHolder)vh).data.Name);
            if (selected) tagsFragment.Add(food);
            else tagsFragment.Remove(food);
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
            intent.putExtra(TAG, foodListFragment.GetSelectedFood().ToArrayList());
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
            foodListFragment.CancelFilter();
        }else{
            clear_button.setVisibility(View.VISIBLE);
            foodListFragment.Filter(keyword);
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
