package personalprojects.seakyluo.randommenu.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import personalprojects.seakyluo.randommenu.Models.AList;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private final AList<Fragment> fragmentList = new AList<>();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void AddFragment(Fragment fragment){ fragmentList.Add(fragment); }
    public void AddFragments(AList<Fragment> fragments){
        fragments.ForEach(this::AddFragment);
    }
    public AList<Fragment> GetFragments() { return fragmentList; }

    @Override
    public Fragment getItem(int i) { return fragmentList.Get(i); }

    @Override
    public int getCount() { return fragmentList.Count(); }
}
