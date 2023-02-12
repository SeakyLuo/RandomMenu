package personalprojects.seakyluo.randommenu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personalprojects.seakyluo.randommenu.adapters.impl.SimpleFoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.R;

public class StringListFragment extends Fragment {
    private SimpleFoodListAdapter adapter = new SimpleFoodListAdapter();
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void setData(AList<String> data){ adapter.setData(data); }
    public void setClickedListener(DataItemClickedListener<String> listener) { adapter.SetOnDataItemClickedListener(listener); }
    public void setOnDeletedClickedListener(DataItemClickedListener<String> listener) { adapter.SetOnDeletedClickedListener(listener); }
    public void Remove(String item) { adapter.remove(item); }
    public void Add(String item) { Remove(item); adapter.add(item, 0); }
}
