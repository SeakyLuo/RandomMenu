package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class SearchActivity extends SwipeBackActivity {
    private static final String HISTORY = "History", ALL = "All", FOOD = "Food", TAG = "Tag", NOTE = "Note";
    private EditText search_bar;
    private ImageButton clear_button;
    private FoodListFragment allFragment, foodFragment, tagFragment, noteFragment;
    private StringListFragment historyFragment;
    private TabPagerAdapter tabPagerAdapter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SwipeBackHelper.getCurrentPage(this).setSwipeEdgePercent(0.2f);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        search_bar = findViewById(R.id.search_bar);
        clear_button = findViewById(R.id.clear_button);

        tabLayout = findViewById(R.id.search_tabs);
        ViewPager viewPager = findViewById(R.id.search_viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabPagerAdapter = new TabPagerAdapter(fragmentManager);
        AddFragments(fragmentManager, savedInstanceState);
        historyFragment.SetData(Settings.settings.SearchHistory);
        historyFragment.SetClickedListener((viewHolder, data) -> {
            search_bar.setText(data);
            search_bar.setSelection(data.length());
            Search(data);
            tabLayout.getTabAt(1).select();
        });
        historyFragment.SetOnDeletedClickedListener((viewHolder, data) -> {
            historyFragment.Remove(data);
            Settings.settings.SearchHistory.Remove(data);
        });
        tabPagerAdapter.GetFragments().After(1).ForEach(f -> {
            FoodListFragment fragment = (FoodListFragment) f;
            fragment.SetFoodClickedListener((viewHolder, food) -> {
                FoodCardDialog dialog = new FoodCardDialog();
                dialog.SetFood(food);
                dialog.SetFoodEditedListener((before, after) -> dialog.SetFood(after));
                dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
            });
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(tabPagerAdapter);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = getKeyword(s);
                if (keyword.isEmpty()){
                    clear_button.setVisibility(View.GONE);
                }else{
                    Settings.settings.SearchHistory.Remove(keyword);
                    Search(Settings.settings.SearchHistory.Add(keyword, 0));
                    clear_button.setVisibility(View.VISIBLE);
                }
            }
        });
        search_bar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                historyFragment.Add(getKeyword(search_bar.getText()));
                return true;
            }
            return false;
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
        findViewById(R.id.search_button).setOnClickListener(v -> {
            if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
            historyFragment.Add(getKeyword(search_bar.getText()));
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
    private String getKeyword(Editable s) { return s.toString().trim(); }

    public void Search(String keyword){
        if (keyword.isEmpty()){
            tabPagerAdapter.GetFragments().After(1).ForEach(f -> ((FoodListFragment) f).Clear());
        }else{
            AList<Food> filtered = Settings.settings.Foods.Find(f -> SearchFoodName(f, keyword) || SearchFoodTag(f, keyword) || SearchFoodNote(f, keyword));
            if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
            allFragment.SetData(filtered);
            foodFragment.SetData(filtered.Find(f -> SearchFoodName(f, keyword)));
            tagFragment.SetData(filtered.Find(f -> SearchFoodTag(f, keyword)));
            noteFragment.SetData(filtered.Find(f -> SearchFoodNote(f, keyword)));
        }
    }

    @Override
    public void finish() {
        Helper.Save(this);
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        try { fragmentManager.putFragment(outState, HISTORY, historyFragment); } catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, ALL, allFragment); } catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, FOOD, foodFragment); } catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, TAG, tagFragment); } catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, NOTE, noteFragment); } catch (IllegalStateException | NullPointerException ignored){}
    }

    private void AddFragments(FragmentManager fragmentManager, Bundle savedInstanceState){
        if (savedInstanceState == null){
            historyFragment = new StringListFragment();
            allFragment = new FoodListFragment();
            foodFragment = new FoodListFragment();
            tagFragment = new FoodListFragment();
            noteFragment = new FoodListFragment();
        }else{
            historyFragment = (StringListFragment) fragmentManager.getFragment(savedInstanceState, HISTORY);
            allFragment = (FoodListFragment) fragmentManager.getFragment(savedInstanceState, ALL);
            foodFragment = (FoodListFragment) fragmentManager.getFragment(savedInstanceState, FOOD);
            tagFragment = (FoodListFragment) fragmentManager.getFragment(savedInstanceState, TAG);
            noteFragment = (FoodListFragment) fragmentManager.getFragment(savedInstanceState, NOTE);
            if (allFragment == null) allFragment = new FoodListFragment();
            if (foodFragment == null) foodFragment = new FoodListFragment();
            if (tagFragment == null) tagFragment = new FoodListFragment();
            if (noteFragment == null) noteFragment = new FoodListFragment();
        }
        tabPagerAdapter.AddFragment(historyFragment);
        tabPagerAdapter.AddFragment(allFragment);
        tabPagerAdapter.AddFragment(foodFragment);
        tabPagerAdapter.AddFragment(tagFragment);
        tabPagerAdapter.AddFragment(noteFragment);
    }
}
