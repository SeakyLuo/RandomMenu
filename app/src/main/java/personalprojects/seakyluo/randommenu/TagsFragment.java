package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class TagsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter();
    private boolean visible = false;
    private boolean isLinear = false;
    public void SetClose(boolean visible){ this.visible = visible; }
    public void SetLinear(boolean isLinear) { this.isLinear = isLinear; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        if (isLinear){
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
        }else{
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        }
        recyclerView.setNestedScrollingEnabled(true);
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void SetTags(List<Tag> data){
        adapter.clear();
        for (Tag tag : data) Add(new ToggleTag(tag, visible));
    }
    public void SetData(AList<Tag> data, boolean visible){
        adapter.clear();
        data.ForEach(tag ->  Add(new ToggleTag(tag, visible)));
    }
    public AList<ToggleTag> GetData() { return adapter.getData(); }
    public AList<Tag> GetTags(){ return adapter.getData().Convert(t -> t); }

    public void AddTag(Tag tag) { adapter.add(new ToggleTag(tag, visible));}
    public void Add(ToggleTag tag){ adapter.add(tag); }
    public void Remove(ToggleTag tag) { adapter.remove(tag); }
    public boolean Contains(Tag tag) { return adapter.getData().Any(t -> t.equals(tag)); }
    public void SetTagClickedListener(TagClickedListener listener) { adapter.SetTagCloseListener(listener); }
}
