package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.TabPagerAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.fragments.SearchRestaurantFragment;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;

public class MyFavoritesActivity extends SwipeBackActivity {
    public static final int REQUEST_CODE = 10;
    private SearchRestaurantFragment restaurantListFragment;
    private FoodListFragment foodListFragment;
    private TabPagerAdapter<Fragment> tabPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab foodTab, restaurantTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        tabLayout = findViewById(R.id.my_favorites_tabs);
        viewPager = findViewById(R.id.my_favorites_viewpager);
        foodTab = tabLayout.newTab();
        restaurantTab = tabLayout.newTab();
        FragmentManager fragmentManager = getSupportFragmentManager();
        foodListFragment = initFoodFragment(fragmentManager, savedInstanceState, "foodListFragment");
        restaurantListFragment = initRestaurantFragment(fragmentManager, savedInstanceState, "restaurantListFragment");
        tabPagerAdapter = new TabPagerAdapter<>(fragmentManager);

        tabLayout.addOnTabSelectedListener(getTabSelectedListener());
        tabLayout.addTab(foodTab);
        tabLayout.addTab(restaurantTab);
        setFoodTabText();
        setRestaurantTab();
        tabPagerAdapter.addFragment(foodListFragment);
        tabPagerAdapter.addFragment(restaurantListFragment);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(tabPagerAdapter);

        foodListFragment.setData(SelfMadeFoodService.getFavoriteFoods());
        foodListFragment.setRemovable(true);
        foodListFragment.setShowLikeImage(false);
        foodListFragment.setFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.setSelfFoodId(food.getId());
            dialog.setFoodLikedListener(after -> {
                foodListFragment.removeFood(after.getId());
                setFoodTabText();
            });
            dialog.showNow(fragmentManager, AskYesNoDialog.TAG);
        });
        foodListFragment.setFoodRemovedListener(data -> {
            data.setFavorite(false);
            SelfMadeFoodService.updateFood(data);
            setFoodTabText();
        });
        foodListFragment.setFoodAddedListener(data -> {
            data.setFavorite(true);
            SelfMadeFoodService.updateFood(data);
            setFoodTabText();
        });

        restaurantListFragment.setData(RestaurantDaoService.selectFavorites());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, "foodListFragment", foodListFragment);
        fragmentManager.putFragment(outState, "restaurantListFragment", restaurantListFragment);
    }

    private void setFoodTabText() {
        foodTab.setText((Tag.format(this, "菜", SelfFoodDaoService.countFavorites())));
    }

    private void setRestaurantTab(){
        restaurantTab.setText((Tag.format(this, "店", RestaurantDaoService.countFavorites()))); //
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


    private FoodListFragment initFoodFragment(FragmentManager fragmentManager, Bundle bundle, String key){
        if (bundle != null){
            FoodListFragment fragment = (FoodListFragment) fragmentManager.getFragment(bundle, key);
            if (fragment != null){
                return fragment;
            }
        }
        return new FoodListFragment();
    }

    private SearchRestaurantFragment initRestaurantFragment(FragmentManager fragmentManager, Bundle savedInstanceState, String key){
        if (savedInstanceState != null){
            SearchRestaurantFragment fragment = (SearchRestaurantFragment) fragmentManager.getFragment(savedInstanceState, key);
            if (fragment != null){
                return fragment;
            }
        }
        return new SearchRestaurantFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        if (requestCode == ActivityCodeConstant.SHOW_RESTAURANT){
            RestaurantVO restaurantVO = data.getParcelableExtra(ShowRestaurantActivity.DATA);
            restaurantListFragment.updateRestaurant(restaurantVO);
        }
    }
}
