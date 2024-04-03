package personalprojects.seakyluo.randommenu.adapters.impl;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.interfaces.SearchFragment;

public class TabPagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    @Getter
    private List<T> fragments = new ArrayList<>();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void addFragment(T fragment){
        fragments.add(fragment);
    }

    public void clear(){
        fragments.clear();
    }

    public void forEach(BiConsumer<Integer, T> consumer){
        for (int i = 0; i < fragments.size(); i++){
            consumer.accept(i, fragments.get(i));
        }
    }

    @Override
    public Fragment getItem(int i) { return fragments.get(i); }

    @Override
    public int getCount() { return fragments.size(); }
}
