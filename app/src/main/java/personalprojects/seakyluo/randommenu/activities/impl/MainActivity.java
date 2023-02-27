package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.fragments.NavigationFragment;
import personalprojects.seakyluo.randommenu.fragments.RandomFragment;
import personalprojects.seakyluo.randommenu.fragments.RestaurantsFragment;
import personalprojects.seakyluo.randommenu.fragments.SettingsFragment;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {
    public RandomFragment randomFragment;
    public NavigationFragment navigationFragment;
    public SettingsFragment settingsFragment;
    public RestaurantsFragment restaurantsFragment;
    private String lastTag;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_random:
                    if (randomFragment == null) {
                        randomFragment = (RandomFragment) fragmentManager.getFragment(savedInstanceState, RandomFragment.TAG);
                        if (randomFragment == null) randomFragment = new RandomFragment();
                    }
                    ShowFragment(randomFragment, RandomFragment.TAG);
                    return true;
                case R.id.navigation_navigation:
                    if (navigationFragment == null) {
                        navigationFragment = (NavigationFragment) fragmentManager.getFragment(savedInstanceState, NavigationFragment.TAG);
                        if (navigationFragment == null)
                            navigationFragment = new NavigationFragment();
                    }
                    ShowFragment(navigationFragment, NavigationFragment.TAG);
                    return true;
                case R.id.navigation_settings:
                    if (settingsFragment == null) {
                        settingsFragment = (SettingsFragment) fragmentManager.getFragment(savedInstanceState, SettingsFragment.TAG);
                        if (settingsFragment == null) settingsFragment = new SettingsFragment();
                    }
                    ShowFragment(settingsFragment, SettingsFragment.TAG);
                    return true;
                case R.id.navigation_restaurants:
                    if (restaurantsFragment == null){
                        restaurantsFragment = (RestaurantsFragment) fragmentManager.getFragment(savedInstanceState, RestaurantsFragment.TAG);
                        if (restaurantsFragment == null) restaurantsFragment = new RestaurantsFragment();
                    }
                    ShowFragment(restaurantsFragment, RestaurantsFragment.TAG);
                    return true;
            }
            return false;
        });
        Helper.init(this);

        if (savedInstanceState == null) {
            randomFragment = new RandomFragment();
            navigationFragment = new NavigationFragment();
            settingsFragment = new SettingsFragment();
            restaurantsFragment = new RestaurantsFragment();
            bottomNavigationView.setSelectedItemId(R.id.navigation_random);
        } else {
            randomFragment = (RandomFragment) fragmentManager.getFragment(savedInstanceState, RandomFragment.TAG);
            navigationFragment = (NavigationFragment) fragmentManager.getFragment(savedInstanceState, NavigationFragment.TAG);
            settingsFragment = (SettingsFragment) fragmentManager.getFragment(savedInstanceState, SettingsFragment.TAG);
            restaurantsFragment = (RestaurantsFragment) fragmentManager.getFragment(savedInstanceState, RestaurantsFragment.TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Helper.save();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Helper.save();
        //Save the fragment's instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            fragmentManager.putFragment(outState, RandomFragment.TAG, randomFragment);
        } catch (IllegalStateException | NullPointerException ignored) {
        }
        try {
            fragmentManager.putFragment(outState, NavigationFragment.TAG, navigationFragment);
        } catch (IllegalStateException | NullPointerException ignored) {
        }
        try {
            fragmentManager.putFragment(outState, SettingsFragment.TAG, settingsFragment);
        } catch (IllegalStateException | NullPointerException ignored) {
        }
    }

    protected void ShowFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (lastTag != null) {
            Fragment lastFragment = fragmentManager.findFragmentByTag(lastTag);
            if (lastFragment != null) transaction.hide(lastFragment);
        }
        lastTag = tag;

        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.content_view, fragment, tag);
        }
        transaction.commit();
    }

    public void ShowFragment(String tag) {
        switch (tag) {
            case RandomFragment.TAG:
                bottomNavigationView.setSelectedItemId(R.id.navigation_random);
                break;
            case NavigationFragment.TAG:
                bottomNavigationView.setSelectedItemId(R.id.navigation_navigation);
                break;
            case SettingsFragment.TAG:
                bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
                break;
        }
    }

    public Fragment GetCurrentFragment() {
        switch (lastTag) {
            case RandomFragment.TAG:
                return randomFragment;
            case NavigationFragment.TAG:
                return navigationFragment;
            case SettingsFragment.TAG:
                return settingsFragment;
            default:
                return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.READ_EXTERNAL_STORAGE_CODE) {
            Helper.init(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            return;
        if (requestCode == ActivityCodeConstant.READ_EXTERNAL_STORAGE_CODE) {
            Helper.init(this);
        }
    }
}
