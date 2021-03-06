package personalprojects.seakyluo.randommenu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personalprojects.seakyluo.randommenu.adapters.SearchFoodListAdapter;

public class SearchFoodListFragment extends BaseFoodListFragment<SearchFoodListAdapter> {
    public static final String TAG = "SearchFoodListFragment";

    public SearchFoodListFragment(){
        adapter = new SearchFoodListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setShowTags(boolean showTags) {
        adapter.setShowTags(showTags);
    }
    public void setShowNote(boolean showNote) {
        adapter.setShowNote(showNote);
    }
    public void setKeyword(String keyword) { adapter.setKeyword(keyword); }
}
