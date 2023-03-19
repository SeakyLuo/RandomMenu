package personalprojects.seakyluo.randommenu.activities.impl;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.TabPagerAdapter;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.fragments.BaseFoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.SearchFoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.StringListFragment;
import personalprojects.seakyluo.randommenu.services.FoodTagService;
import personalprojects.seakyluo.randommenu.services.SelfFoodService;
import personalprojects.seakyluo.randommenu.utils.SearchUtils;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.MatchFood;
import personalprojects.seakyluo.randommenu.models.Settings;

public class SearchActivity extends SwipeBackActivity {
    private static final String HISTORY = "History", ALL = "All", FOOD = "Food", TAG = "Tag", NOTE = "Note";
    private EditText search_bar;
    private ImageButton clear_button;
    private SearchFoodListFragment allFragment, foodFragment, tagFragment, noteFragment;
    private StringListFragment historyFragment;
    private TabPagerAdapter<Fragment> tabPagerAdapter;
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
        tabPagerAdapter = new TabPagerAdapter<>(fragmentManager);
        addFragments(fragmentManager, savedInstanceState);
        historyFragment.setData(Settings.settings.SearchHistory);
        historyFragment.setClickedListener((viewHolder, data) -> {
            search_bar.setText(data);
            search_bar.setSelection(data.length());
            search(data);
            tabLayout.getTabAt(1).select();
        });
        historyFragment.setOnDeletedClickedListener((viewHolder, data) -> {
            historyFragment.Remove(data);
            Settings.settings.SearchHistory.remove(data);
        });
        getSearchFragments().forEach(fragment -> {
            fragment.setFoodClickedListener((viewHolder, food) -> foodClicked(food));
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
                    Settings.settings.SearchHistory.move(keyword, 0);
                    search(keyword);
                    clear_button.setVisibility(View.VISIBLE);
                }
            }
        });
        search_bar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                historyFragment.Add(getKeyword());
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

    public static String getKeyword(Editable s) { return s.toString().trim(); }
    public String getKeyword() { return getKeyword(search_bar.getText()); }
    public Stream<SearchFoodListFragment> getSearchFragments() { return tabPagerAdapter.getFragments().after(0).stream().map(f -> (SearchFoodListFragment)f); }

    private void foodClicked(SelfFood food){
        FoodCardDialog dialog = new FoodCardDialog();
        dialog.setFoodId(food.getId());
        dialog.setFoodEditedListener(after -> {
            getSearchFragments().forEach(f -> f.updateFood(after));
        });
        dialog.setFoodLikedListener(after -> {
            getSearchFragments().forEach(f -> f.updateFood(after));
        });
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
    }

    public void search(String keyword){
        if (keyword.isEmpty()){
            getSearchFragments().forEach(BaseFoodListFragment::clear);
            return;
        }
        getSearchFragments().forEach(f -> f.setKeyword(keyword));
        if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
        List<MatchFood> byFood = new ArrayList<>(), byTag = new ArrayList<>(), byNote = new ArrayList<>(), all = new ArrayList<>();
        for (SelfFood f : SelfFoodDaoService.selectAll()) {
            MatchFood mf = SearchUtils.evalFood(f, keyword);
            SelfFood food = mf.getFood();
            if (mf.getNamePoints() > 0){
                byFood.add(new MatchFood(food, mf.getNamePointsWithBonus()));
            }
            if (mf.getTagPoints() > 0){
                byTag.add(new MatchFood(food, mf.getTagPointsWithBonus()));
            }
            if (mf.getNotePoints() > 0){
                byNote.add(new MatchFood(food, mf.getNotePointsWithBonus()));
            }
            if (mf.getPoints() > 0){
                all.add(new MatchFood(food, mf.getPointsWithBonus()));
            }
        }
        allFragment.setData(sortFoods(all));
        foodFragment.setData(sortFoods(byFood));
        tagFragment.setData(sortFoods(byTag));
        noteFragment.setData(sortFoods(byNote));
    }

    public static List<SelfFood> sortFoods(List<MatchFood> foods){
        return foods.stream().sorted(Comparator.comparing(MatchFood::getPoints).reversed())
                .map(MatchFood::getFood)
                .peek(f -> f.setTags(FoodTagService.selectByFood(f.getId())))
                .collect(Collectors.toList());
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

    private void addFragments(FragmentManager fragmentManager, Bundle savedInstanceState){
        if (savedInstanceState == null){
            historyFragment = new StringListFragment();
            initAllFragment();
            initFoodFragment();
            initTagFragment();
            initNoteFragment();
        }else{
            historyFragment = (StringListFragment) fragmentManager.getFragment(savedInstanceState, HISTORY);
            allFragment = (SearchFoodListFragment) fragmentManager.getFragment(savedInstanceState, ALL);
            foodFragment = (SearchFoodListFragment) fragmentManager.getFragment(savedInstanceState, FOOD);
            tagFragment = (SearchFoodListFragment) fragmentManager.getFragment(savedInstanceState, TAG);
            noteFragment = (SearchFoodListFragment) fragmentManager.getFragment(savedInstanceState, NOTE);
            if (allFragment == null) initAllFragment();
            if (foodFragment == null) initFoodFragment();
            if (tagFragment == null) initTagFragment();
            if (noteFragment == null) initNoteFragment();
        }
        tabPagerAdapter.addFragment(historyFragment);
        tabPagerAdapter.addFragment(allFragment);
        tabPagerAdapter.addFragment(foodFragment);
        tabPagerAdapter.addFragment(tagFragment);
        tabPagerAdapter.addFragment(noteFragment);
    }

    private void initAllFragment(){
        allFragment = new SearchFoodListFragment();
        allFragment.setShowTags(true);
        allFragment.setShowNote(true);
    }

    private void initFoodFragment(){
        foodFragment = new SearchFoodListFragment();
        foodFragment.setShowTags(true);
        foodFragment.setShowNote(true);
    }

    private void initTagFragment(){
        tagFragment = new SearchFoodListFragment();
        tagFragment.setShowTags(true);
        tagFragment.setShowNote(false);
    }

    private void initNoteFragment(){
        noteFragment = new SearchFoodListFragment();
        noteFragment.setShowTags(false);
        noteFragment.setShowNote(true);
    }

}
