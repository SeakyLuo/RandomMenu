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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.TabPagerAdapter;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.database.services.SearchHistoryDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.fragments.SearchFoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.SearchRestaurantFragment;
import personalprojects.seakyluo.randommenu.fragments.StringListFragment;
import personalprojects.seakyluo.randommenu.interfaces.SearchFragment;
import personalprojects.seakyluo.randommenu.models.MatchResult;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.SearchUtils;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.MatchFood;

public class SearchActivity extends AppCompatActivity {
    public static final String SEARCH_TYPE = "SEARCH_TYPE";
    private EditText searchBar;
    private ImageButton clearButton;
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
        addFragments(fragmentManager, savedInstanceState, foodClass);
        backButton.setOnClickListener(v -> finish());
        historyFragment.setData(SearchHistoryDaoService.list(foodClass));
        historyFragment.setItemClickedListener(this::clickHistoryItem);
        historyFragment.setItemDeletedListener((viewHolder, data) -> SearchHistoryDaoService.delete(foodClass, data));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(tabPagerAdapter);
        searchBar.addTextChangedListener(getSearchBarTextWatcher());
        searchBar.setOnEditorActionListener(this::onSearchBarEdited);
        tabLayout.addOnTabSelectedListener(getTabSelectedListener());
        searchButton.setOnClickListener(this::searchClicked);
        clearButton.setOnClickListener(this::clearClicked);
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
                    SearchHistoryDaoService.delete(foodClass, keyword);
                    SearchHistoryDaoService.insert(foodClass, keyword);
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

    private void clickHistoryItem(CustomAdapter<String>.CustomViewHolder viewHolder, String data) {
        searchBar.setText(data);
        searchBar.setSelection(data.length());
        search(data);
        tabLayout.getTabAt(1).select();
    }

    private Stream<SearchFoodListFragment> getFoodListFragments() {
        return tabPagerAdapter.getFragments().stream().filter(f -> f instanceof SearchFoodListFragment).map(f -> (SearchFoodListFragment) f);
    }

    private List<SearchFragment> getSearchFragments() {
        return tabPagerAdapter.getFragments().stream().filter(f -> f instanceof SearchFragment).map(f -> (SearchFragment) f).collect(Collectors.toList());
    }

    private void search(String keyword){
        List<SearchFragment> fragments = getSearchFragments();
        if (keyword.isEmpty()){
            fragments.forEach(SearchFragment::clear);
            return;
        }
        fragments.forEach(f -> f.setKeyword(keyword));
        if (foodClass == FoodClass.SELF_MADE){
            List<MatchFood> byFood = new ArrayList<>(), byTag = new ArrayList<>(), byNote = new ArrayList<>(), all = new ArrayList<>();
            for (SelfMadeFood f : SelfFoodDaoService.selectAll()) {
                MatchFood mf = SearchUtils.evalFood(f, keyword);
                SelfMadeFood food = mf.getData();
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
            fragments.get(0).setSearchResult(all);
            fragments.get(1).setSearchResult(byFood);
            fragments.get(2).setSearchResult(byTag);
            fragments.get(3).setSearchResult(byNote);
        }
        else if (foodClass == FoodClass.RESTAURANT){
            List<MatchResult<RestaurantVO>> all = RestaurantDaoService.search(keyword).stream()
                    .map(r -> SearchUtils.evalRestaurant(r, keyword))
                    .collect(Collectors.toList());
            fragments.get(0).setSearchResult(all);
        }
        if (tabLayout.getTabAt(0).isSelected()) tabLayout.getTabAt(1).select();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabPagerAdapter.forEach((i, fragment) -> {
            try {
                fragmentManager.putFragment(outState, String.valueOf(i), fragment);
            } catch (IllegalStateException | NullPointerException ignored){

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_left_out);
    }

    private void addFragments(FragmentManager fragmentManager, Bundle bundle, FoodClass foodClass){
        historyFragment = Optional.ofNullable(bundle == null ? null : (StringListFragment) fragmentManager.getFragment(bundle, "0"))
                .orElse(new StringListFragment());
        tabPagerAdapter.addFragment(historyFragment);
        if (foodClass == FoodClass.SELF_MADE){
            addSearchFragment(initFoodFragment(fragmentManager, bundle, "1", true, true, "全部"));
            addSearchFragment(initFoodFragment(fragmentManager, bundle, "2", true, true, "菜肴"));
            addSearchFragment(initFoodFragment(fragmentManager, bundle, "3", true, false, "标签"));
            addSearchFragment(initFoodFragment(fragmentManager, bundle, "4", false, true, "备注"));
        }
        else if (foodClass == FoodClass.RESTAURANT){
            addSearchFragment(initRestaurantFragment(fragmentManager, bundle, "1", "全部"));
        }
    }

    private <T extends Fragment & SearchFragment> void addSearchFragment(T searchFragment){
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(searchFragment.getName());
        tabLayout.addTab(tab);
        tabPagerAdapter.addFragment(searchFragment);
    }

    private SearchFoodListFragment initFoodFragment(FragmentManager fragmentManager, Bundle bundle, String key,
                                                    boolean showTags, boolean showNote, String name){
        if (bundle != null){
            SearchFoodListFragment fragment = (SearchFoodListFragment) fragmentManager.getFragment(bundle, key);
            if (fragment != null){
                return fragment;
            }
        }
        SearchFoodListFragment fragment = new SearchFoodListFragment();
        fragment.setName(name);
        fragment.setShowTags(showTags);
        fragment.setShowNote(showNote);
        fragment.setFoodClickedListener((vh, data) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.setSelfFoodId(data.getId());
            dialog.setFoodEditedListener(after -> {
                getFoodListFragments().forEach(f -> f.updateFood(after));
            });
            dialog.setFoodLikedListener(after -> {
                getFoodListFragments().forEach(f -> f.updateFood(after));
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        return fragment;
    }

    private SearchRestaurantFragment initRestaurantFragment(FragmentManager fragmentManager, Bundle savedInstanceState, String key,
                                                            String name){
        if (savedInstanceState != null){
            SearchRestaurantFragment fragment = (SearchRestaurantFragment) fragmentManager.getFragment(savedInstanceState, key);
            if (fragment != null){
                return fragment;
            }
        }
        SearchRestaurantFragment fragment = new SearchRestaurantFragment();
        fragment.setName(name);
        return fragment;
    }

}
