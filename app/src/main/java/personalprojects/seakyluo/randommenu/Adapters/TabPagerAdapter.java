package personalprojects.seakyluo.randommenu.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import personalprojects.seakyluo.randommenu.models.AList;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private final AList<Fragment> fragmentList = new AList<>();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void AddFragment(Fragment fragment){ fragmentList.add(fragment); }
    public void AddFragments(AList<Fragment> fragments){
        fragments.forEach(this::AddFragment);
    }
    public AList<Fragment> GetFragments() { return fragmentList; }

    @Override
    public Fragment getItem(int i) { return fragmentList.get(i); }

    @Override
    public int getCount() { return fragmentList.count(); }
}
