package personalprojects.seakyluo.randommenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import personalprojects.seakyluo.randommenu.Models.AList;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private final AList<Fragment> fragmentList = new AList<>();
    private final AList<String> fragmentTitles = new AList<>();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void AddFragment(Fragment fragment, String title){
        fragmentList.Add(fragment);
        fragmentTitles.Add(title);
    }

    public void AddFragments(AList<Fragment> fragments, AList<String> titles){
        fragmentList.AddAll(fragments);
        fragmentTitles.AddAll(titles);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.Get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.Count();
    }
}
