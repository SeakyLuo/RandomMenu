package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import personalprojects.seakyluo.randommenu.Fragments.NavigationFragment;
import personalprojects.seakyluo.randommenu.Fragments.RandomFragment;
import personalprojects.seakyluo.randommenu.Fragments.SettingsFragment;
import personalprojects.seakyluo.randommenu.Helpers.Helper;

public class MainActivity extends AppCompatActivity {
    public RandomFragment randomFragment;
    public NavigationFragment navigationFragment;
    public SettingsFragment settingsFragment;
    private String lastTag;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.navigation_random:
                    if (randomFragment == null){
                        randomFragment = (RandomFragment) fragmentManager.getFragment(savedInstanceState, RandomFragment.TAG);
                        if (randomFragment == null) randomFragment = new RandomFragment();
                    }
                    ShowFragment(randomFragment, RandomFragment.TAG);
                    return true;
                case R.id.navigation_navigation:
                    if (navigationFragment == null){
                        navigationFragment = (NavigationFragment) fragmentManager.getFragment(savedInstanceState, NavigationFragment.TAG);
                        if (navigationFragment == null) navigationFragment = new NavigationFragment();
                    }
                    ShowFragment(navigationFragment, NavigationFragment.TAG);
                    return true;
                case R.id.navigation_settings:
                    if (settingsFragment == null){
                        settingsFragment = (SettingsFragment) fragmentManager.getFragment(savedInstanceState, SettingsFragment.TAG);
                        if (settingsFragment == null) settingsFragment = new SettingsFragment();
                    }
                    ShowFragment(settingsFragment, SettingsFragment.TAG);
                    return true;
            }
            return false;
        });
        Helper.Init(this);

        if (savedInstanceState == null) {
            randomFragment = new RandomFragment();
            navigationFragment = new NavigationFragment();
            settingsFragment = new SettingsFragment();
            bottomNavigationView.setSelectedItemId(R.id.navigation_random);
        }else{
            randomFragment = (RandomFragment) fragmentManager.getFragment(savedInstanceState, RandomFragment.TAG);
            navigationFragment = (NavigationFragment) fragmentManager.getFragment(savedInstanceState, NavigationFragment.TAG);
            settingsFragment = (SettingsFragment) fragmentManager.getFragment(savedInstanceState, SettingsFragment.TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Helper.Save();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Helper.Save();
        //Save the fragment's instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        try { fragmentManager.putFragment(outState, RandomFragment.TAG, randomFragment); }catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, NavigationFragment.TAG, navigationFragment); }catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, SettingsFragment.TAG, settingsFragment); }catch (IllegalStateException | NullPointerException ignored){}
    }

    protected void ShowFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (lastTag != null){
            Fragment lastFragment = fragmentManager.findFragmentByTag(lastTag);
            if (lastFragment != null) transaction.hide(lastFragment);
        }
        lastTag = tag;

        if (fragment.isAdded()){
            transaction.show(fragment);
        }else{
            transaction.add(R.id.content_view, fragment, tag);
        }
        transaction.commit();
    }

    public void ShowFragment(String tag){
        switch (tag){
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

    public Fragment GetCurrentFragment(){
        switch (lastTag){
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
}
