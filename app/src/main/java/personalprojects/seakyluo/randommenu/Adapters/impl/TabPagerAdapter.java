package personalprojects.seakyluo.randommenu.adapters.impl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import personalprojects.seakyluo.randommenu.models.AList;

public class TabPagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    private final AList<T> fragmentList = new AList<>();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(T fragment){ fragmentList.add(fragment); }
    public void addFragments(AList<T> fragments){
        fragments.ForEach(this::addFragment);
    }
    public AList<T> getFragments() { return fragmentList; }

    @Override
    public Fragment getItem(int i) { return fragmentList.get(i); }

    @Override
    public int getCount() { return fragmentList.size(); }
}
