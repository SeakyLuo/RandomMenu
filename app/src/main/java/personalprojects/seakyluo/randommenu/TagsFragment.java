package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class TagsFragment extends Fragment {
    private TagAdapter adapter = new TagAdapter();
    public void SetCloseable(boolean closeable){ adapter.SetCloseable(closeable); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        return view;
    }
    public void SetData(AList<Tag> data){ adapter.SetData(data); }
    public void SetData(List<Tag> data){ adapter.SetData(data); }
    public AList<Tag> GetData() { return adapter.GetData(); }

    public void SetTagClickedListener(OnDataItemClickedListener<Tag> listener) { adapter.SetTagClickedListener(listener); }
    public void SetTagCloseListener(OnDataItemClickedListener<Tag> listener) { adapter.SetTagCloseListener(listener); }
}
