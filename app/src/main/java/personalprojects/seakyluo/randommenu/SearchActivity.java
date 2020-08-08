package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.adapters.TabPagerAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.StringListFragment;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.SearchHelper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

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
            Settings.settings.SearchHistory.remove(data);
        });
        tabPagerAdapter.GetFragments().after(0).forEach(f -> {
            FoodListFragment fragment = (FoodListFragment) f;
            fragment.setFoodClickedListener((viewHolder, food) -> {
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
                    Settings.settings.SearchHistory.remove(keyword);
                    Search(Settings.settings.SearchHistory.add(keyword, 0));
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

    private static boolean SearchFoodName(Food food, String keyword) { return food.Name.contains(keyword);}
    private static boolean SearchFoodTag(Food food, String keyword) { return food.GetTags().any(t -> t.Name.contains(keyword)); }
    private static boolean SearchFoodNote(Food food, String keyword) { return food.Note.contains(keyword); }
    public static String getKeyword(Editable s) { return s.toString().trim(); }

    public void Search(String keyword){
        if (keyword.isEmpty()){
            tabPagerAdapter.GetFragments().after(0).forEach(f -> ((FoodListFragment) f).Clear());
        }else{
            if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
            AList<Food> food = new AList<>(), tag = new AList<>(), note = new AList<>();
            List<MatchFood> all = new ArrayList<>();
            Settings.settings.Foods.forEach(f -> {
                if (SearchFoodName(f, keyword)){
                    food.add(f);
                }
                if (SearchFoodTag(f, keyword)){
                    tag.add(f);
                }
                if (SearchFoodNote(f, keyword)){
                    note.add(f);
                }
                int points = SearchHelper.evalFood(f, keyword);
                if (points > 0){
                    all.add(new MatchFood(f, points));
                }
            });
            allFragment.setData(new AList<>(all.stream().sorted((f1, f2) -> (f2.points - f1.points)).map(f -> f.food).collect(Collectors.toList())));
            foodFragment.setData(food);
            tagFragment.setData(tag);
            noteFragment.setData(note);
        }
    }

    @Override
    public void finish() {
        Helper.Save();
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

    private static class MatchFood{
        public Food food;
        public int points;
        public MatchFood(Food food, int points){
            this.food = food;
            this.points = points;
        }
    }
}
