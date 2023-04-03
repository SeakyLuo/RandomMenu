package personalprojects.seakyluo.randommenu.activities;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.TabPagerAdapter;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.fragments.BaseFoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.SearchFoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.StringListFragment;
import personalprojects.seakyluo.randommenu.services.FoodTagService;
import personalprojects.seakyluo.randommenu.utils.SearchUtils;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.MatchFood;
import personalprojects.seakyluo.randommenu.models.Settings;

public class SearchActivity extends AppCompatActivity {
    public static final String SEARCH_TYPE = "SEARCH_TYPE";
    private static final String HISTORY = "History", ALL = "All", FOOD = "Food", TAG = "Tag", NOTE = "Note";
    private EditText searchBar;
    private ImageButton clearButton;
    private SearchFoodListFragment allFragment, foodFragment, tagFragment, noteFragment;
    private StringListFragment historyFragment;
    private TabPagerAdapter<Fragment> tabPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FoodClass foodClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ImageButton backButton = findViewById(R.id.back_button);
        searchBar = findViewById(R.id.search_bar);
        clearButton = findViewById(R.id.clear_button);
        tabLayout = findViewById(R.id.search_tabs);
        viewPager = findViewById(R.id.search_viewpager);
        ImageButton searchButton = findViewById(R.id.search_button);

        foodClass = (FoodClass) getIntent().getSerializableExtra(SEARCH_TYPE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabPagerAdapter = new TabPagerAdapter<>(fragmentManager);
        addFragments(fragmentManager, savedInstanceState);
        backButton.setOnClickListener(v -> finish());
        historyFragment.setData(Settings.settings.SearchHistory);
        historyFragment.setItemClickedListener(this::clickHistoryItem);
        historyFragment.setItemDeletedListener(this::removeHistoryItem);
        getFoodListFragments().forEach(fragment -> fragment.setFoodClickedListener((viewHolder, food) -> foodClicked(food)));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(tabPagerAdapter);
        searchBar.addTextChangedListener(getSearchBarTextWatcher());
        searchBar.setOnEditorActionListener(this::onSearchBarEdited);
        tabLayout.addOnTabSelectedListener(getTabSelectedListener());
        searchButton.setOnClickListener(this::searchClicked);
        clearButton.setOnClickListener(this::clearClicked);
    }

    private void removeHistoryItem(CustomAdapter<String>.CustomViewHolder viewHolder, String data) {
        Settings.settings.SearchHistory.remove(data);
    }

    private TabLayout.OnTabSelectedListener getTabSelectedListener() {
        return new TabLayout.OnTabSelectedListener() {
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
        };
    }

    private TextWatcher getSearchBarTextWatcher() {
        return new TextWatcher() {
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
                    clearButton.setVisibility(View.GONE);
                } else {
                    Settings.settings.SearchHistory.move(keyword, 0);
                    search(keyword);
                    clearButton.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void searchClicked(View v){
        String keyword = getKeyword();
        if (keyword.isEmpty()){
            return;
        }
        if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
        historyFragment.add(keyword);
    }

    private void clearClicked(View v){
        searchBar.setText("");
        clearButton.setVisibility(View.GONE);
    }

    private boolean onSearchBarEdited(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            historyFragment.add(getKeyword());
            return true;
        }
        return false;
    }

    private String getKeyword() {
        return searchBar.getText().toString().trim();
    }

    private Stream<SearchFoodListFragment> getFoodListFragments() {
        return tabPagerAdapter.getFragments().stream().filter(f -> f instanceof SearchFoodListFragment).map(f -> (SearchFoodListFragment) f);
    }

    private void foodClicked(SelfMadeFood food){
        FoodCardDialog dialog = new FoodCardDialog();
        dialog.setSelfFoodId(food.getId());
        dialog.setFoodEditedListener(after -> {
            getFoodListFragments().forEach(f -> f.updateFood(after));
        });
        dialog.setFoodLikedListener(after -> {
            getFoodListFragments().forEach(f -> f.updateFood(after));
        });
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
    }

    private void search(String keyword){
        if (keyword.isEmpty()){
            getFoodListFragments().forEach(BaseFoodListFragment::clear);
            return;
        }
        getFoodListFragments().forEach(f -> f.setKeyword(keyword));
        if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
        List<MatchFood> byFood = new ArrayList<>(), byTag = new ArrayList<>(), byNote = new ArrayList<>(), all = new ArrayList<>();
        for (SelfMadeFood f : SelfFoodDaoService.selectAll()) {
            MatchFood mf = SearchUtils.evalFood(f, keyword);
            SelfMadeFood food = mf.getFood();
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

    private static List<SelfMadeFood> sortFoods(List<MatchFood> foods){
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_left_out);
    }

    private void addFragments(FragmentManager fragmentManager, Bundle savedInstanceState){
        if (savedInstanceState == null){
            historyFragment = new StringListFragment();
            initAllFragment();
            initFoodFragment();
            initTagFragment();
            initNoteFragment();
        } else {
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

    private void clickHistoryItem(CustomAdapter<String>.CustomViewHolder viewHolder, String data) {
        searchBar.setText(data);
        searchBar.setSelection(data.length());
        search(data);
        tabLayout.getTabAt(1).select();
    }
}
