package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class TagsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter();
    private int spanCount = 2;
    public void SetCloseable(boolean closeable){ adapter.SetCloseable(closeable); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL));
        return view;
    }
    public AList<Tag> GetData() { return adapter.GetData(); }
    public TagAdapter GetAdapter() { return adapter; }
    public RecyclerView GetRecyclerView() { return recyclerView; }
    public void Add(Tag tag, int index) { adapter.Add(tag, index); }
    public void Remove(Tag tag) { adapter.Remove(tag); }
    public boolean Contains(Tag tag) { return adapter.Contains(tag); }
    public void SetData(AList<Tag> data){ adapter.SetData(data); }
    public void SetData(List<Tag> data){ adapter.SetData(data); }
    public void SetSpanCount(int spanCount) { this.spanCount = spanCount; }
    public void SetTagClickedListener(OnDataItemClickedListener<Tag> listener) { adapter.SetTagClickedListener(listener); }
    public void SetTagCloseListener(OnDataItemClickedListener<Tag> listener) { adapter.SetTagCloseListener(listener); }
}
