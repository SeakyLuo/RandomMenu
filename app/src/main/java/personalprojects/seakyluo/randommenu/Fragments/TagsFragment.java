package personalprojects.seakyluo.randommenu.fragments;

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

import personalprojects.seakyluo.randommenu.adapters.impl.TagAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class TagsFragment extends Fragment {
    public static final String TAG = "TagsFragment";
    public RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter();
    private int spanCount = 2;
    public void SetCloseable(boolean closeable){ adapter.SetCloseable(closeable); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL));
        return view;
    }
    public AList<Tag> getData() { return adapter.getData(); }
    public TagAdapter GetAdapter() { return adapter; }
    public void add(Tag tag, int index) { adapter.add(tag, index); }
    public void add(Tag tag) { adapter.add(tag); }
    public void move(int from, int to) { adapter.move(from, to); }
    public void remove(Tag tag) { adapter.remove(tag); }
    public boolean contains(Tag tag) { return adapter.contains(tag); }
    public int indexOf(Tag tag) { return adapter.indexOf(tag); }
    public void setData(AList<Tag> data){ adapter.setData(data); }
    public void setData(List<Tag> data){ adapter.setData(data); }
    public void setSpanCount(int spanCount) { this.spanCount = spanCount; }
    public void setTagClickedListener(DataItemClickedListener<Tag> listener) { adapter.setTagClickedListener(listener); }
    public void setTagClosedListener(DataItemClickedListener<Tag> listener) { adapter.SetTagCloseListener(listener); }
}
