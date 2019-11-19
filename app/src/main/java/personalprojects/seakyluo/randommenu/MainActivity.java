package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private RandomFragment randomFragment;
    private NavigationFragment navigationFragment;
    private SettingsFragment settingsFragment;
    private String lastTag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_random:
                        ShowFragment(randomFragment, RandomFragment.TAG, lastTag);
                        return true;
                    case R.id.navigation_navigation:
                        ShowFragment(navigationFragment, NavigationFragment.TAG, lastTag);
                        return true;
                    case R.id.navigation_settings:
                        ShowFragment(settingsFragment, SettingsFragment.TAG, lastTag);
                        return true;
                }
                return false;
            }
        });

        DbHelper.Init(getApplicationContext());

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            randomFragment = (RandomFragment)fragmentManager.getFragment(savedInstanceState, RandomFragment.TAG);
            navigationFragment = (NavigationFragment)fragmentManager.getFragment(savedInstanceState, NavigationFragment.TAG);
            settingsFragment = (SettingsFragment)fragmentManager.getFragment(savedInstanceState, SettingsFragment.TAG);
        }else{
            randomFragment = new RandomFragment();
            navigationFragment = new NavigationFragment();
            settingsFragment = new SettingsFragment();
            navView.setSelectedItemId(R.id.navigation_random);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        try { fragmentManager.putFragment(outState, RandomFragment.TAG, randomFragment); }catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, NavigationFragment.TAG, navigationFragment); }catch (IllegalStateException | NullPointerException ignored){}
        try { fragmentManager.putFragment(outState, SettingsFragment.TAG, settingsFragment); }catch (IllegalStateException | NullPointerException ignored){}
    }


    protected void ShowFragment(Fragment fragment, String tag, String lastTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (lastTag != null) {
            Fragment lastFragment = fragmentManager.findFragmentByTag(lastTag);
            if (lastFragment != null) {
                transaction.hide(lastFragment);
            }
        }
        this.lastTag = tag;

        if (fragment.isAdded()) {
            transaction.show(fragment);
        }
        else {
            transaction.add(R.id.content_view, fragment, tag);
        }
        transaction.commit();
    }

}
