package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class TagFragment extends Fragment {
    private RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter();
    private boolean showCloseButton = false;
    public void SetClose(boolean visible){
        showCloseButton = visible;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        SetRecyclerView(recyclerView);
        return view;
    }

    public void SetData(List<Tag> data, boolean visible){ for (Tag tag : data) Add(new ToggleTag(tag, visible)); }
    public void SetData(List<ToggleTag> data){ adapter.setData(data); }
    public List<ToggleTag> GetData() { return adapter.getData(); }
    public List<Tag> GetTags(){
        List<Tag> tags = new ArrayList<>();
        for (ToggleTag tag: adapter.getData())
            tags.add(tag.ToTag());
        return tags;
    }

    public void Add(ToggleTag tag){ adapter.add(tag); }
    public void Remove(ToggleTag tag) { adapter.remove(tag); }
    public int CountTags() { return adapter.getData().size(); }

    private void SetRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setNestedScrollingEnabled(true);
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);
    }
}
