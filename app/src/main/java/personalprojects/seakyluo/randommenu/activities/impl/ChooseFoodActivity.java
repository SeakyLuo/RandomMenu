package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.TagsFragment;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.services.SelfFoodService;

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
        AList<SelfFood> foods = new AList<>(getIntent().getParcelableArrayListExtra(TAG));

        tagsFragment.setSpanCount(1);
        tagsFragment.setCloseable(true);
        tagsFragment.setData(foods.convert(f -> new Tag(f.getName())));
        tagsFragment.setTagClosedListener((vh, food) -> foodListFragment.unselectFood(food.getName()));
        foodListFragment.setData(SelfFoodDaoService.selectAll());
        foodListFragment.setSelectedFood(foods);
        foodListFragment.setFoodSelectedListener((vh, selected) -> {
            Tag food = new Tag(vh.getData().getName());
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
            intent.putParcelableArrayListExtra(TAG, foodListFragment.getSelectedFoods());
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
