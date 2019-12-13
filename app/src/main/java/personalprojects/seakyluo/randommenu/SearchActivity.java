package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import personalprojects.seakyluo.randommenu.Interfaces.BooleanLambda;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class SearchActivity extends SwipeBackActivity {
    private EditText search_bar;
    private ImageButton clear_button;
    private FoodListFragment allFragment, foodFragment, tagFragment, noteFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        search_bar = findViewById(R.id.search_bar);
        clear_button = findViewById(R.id.clear_button);

        TabLayout tabLayout = findViewById(R.id.search_tabs);
        TabViewPager viewPager = findViewById(R.id.search_viewpager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        tabPagerAdapter.AddFragment(allFragment = new FoodListFragment());
        tabPagerAdapter.AddFragment(foodFragment = new FoodListFragment());
        tabPagerAdapter.AddFragment(tagFragment = new FoodListFragment());
        tabPagerAdapter.AddFragment(noteFragment = new FoodListFragment());
        tabPagerAdapter.GetFragments().ForEach(f -> {
            FoodListFragment fragment = (FoodListFragment) f;
            fragment.SetFoodClickedListener((viewHolder, food) -> {
                FoodCardDialog dialog = new FoodCardDialog();
                dialog.SetFood(food);
                dialog.SetFoodEditedListener((before, after) -> dialog.SetFood(after));
                dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
            });
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setSwipeable(false);
        viewPager.setAdapter(tabPagerAdapter);
        search_bar.requestFocus();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()){
                    tabPagerAdapter.GetFragments().ForEach(f -> ((FoodListFragment) f).Clear());
                    clear_button.setVisibility(View.GONE);
                }else{
                    AList<Food> filtered = Settings.settings.Foods.Filter(f -> SearchFoodName(f, keyword) || SearchFoodTag(f, keyword) || SearchFoodNote(f, keyword) );
                    allFragment.SetData(filtered);
                    foodFragment.SetData(filtered.Filter(f -> SearchFoodName(f, keyword)));
                    tagFragment.SetData(filtered.Filter(f -> SearchFoodTag(f, keyword)));
                    noteFragment.SetData(filtered.Filter(f -> SearchFoodNote(f, keyword)));
                    clear_button.setVisibility(View.VISIBLE);
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position, true);
                tab.select();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        clear_button.setVisibility(View.GONE);
        clear_button.setOnClickListener(v -> {
            search_bar.setText("");
            clear_button.setVisibility(View.GONE);
        });
    }

    private boolean SearchFoodName(Food food, String keyword) { return food.Name.contains(keyword);}
    private boolean SearchFoodTag(Food food, String keyword) { return food.GetTags().Any(t -> t.Name.contains(keyword)); }
    private boolean SearchFoodNote(Food food, String keyword) { return food.Note.contains(keyword); }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
