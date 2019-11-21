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

public class TagFragment extends Fragment {
    private RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter();
    private boolean showCloseButton = false;
    private boolean isLinear = false;
    public void SetClose(boolean visible){
        showCloseButton = visible;
    }
    public void SetLinear(boolean isLinear) { this.isLinear = isLinear; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        SetRecyclerView(recyclerView);
        return view;
    }

    public void SetTags(List<Tag> data){ for (Tag tag : data) Add(new ToggleTag(tag, showCloseButton)); }
    public void SetData(List<Tag> data, boolean visible){ for (Tag tag : data) Add(new ToggleTag(tag, visible)); }
    public void SetData(List<ToggleTag> data){ adapter.setData(data); }
    public AList<ToggleTag> GetData() { return adapter.getData(); }
    public AList<Tag> GetTags(){ return adapter.getData().Convert(ToggleTag::ToTag); }

    public void AddTag(Tag tag) { adapter.add(new ToggleTag(tag, showCloseButton));}
    public void Add(ToggleTag tag){ adapter.add(tag); }
    public void Remove(ToggleTag tag) { adapter.remove(tag); }
    public void RemoveTag(Tag target){ adapter.getData().Remove(tag -> tag.Name.equals(target.Name)); }
    public boolean Contains(ToggleTag tag) { return adapter.getData().Contains(tag); }
    public void SetTagClickedListener(TagClickedListener listener) { adapter.SetTagClickedListener(listener); }

    private void SetRecyclerView(RecyclerView recyclerView) {
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
    }
}
