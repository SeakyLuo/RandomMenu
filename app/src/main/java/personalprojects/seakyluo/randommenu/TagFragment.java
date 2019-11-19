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

import java.util.List;

public class TagFragment extends Fragment {
    private RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        SetRecyclerView(recyclerView);
        return view;
    }

    public void SetData(List<Tag> data){

    }

    public void SetEditable(boolean editable){

    }

    private void SetRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setNestedScrollingEnabled(true);
        adapter.setActivity(getActivity());
        adapter.add(new Tag[] { new Tag("tag"), new Tag("tag1"), new Tag("tag22"), new Tag("tag333"), new Tag("tag4444"),
                new Tag("tag55555"), new Tag("tag666666"), new Tag("tag7777777"), new Tag("tag88888888"), new Tag("tag999999999")} );
        recyclerView.setAdapter(adapter);
    }
}
