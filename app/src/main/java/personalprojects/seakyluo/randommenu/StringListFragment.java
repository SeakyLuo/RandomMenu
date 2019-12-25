package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;

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

    public void SetData(AList<String> data){ adapter.SetData(data); }
    public void SetClickedListener(OnDataItemClickedListener<String> listener) { adapter.SetOnDataItemClickedListener(listener); }
    public void SetOnDeletedClickedListener(OnDataItemClickedListener<String> listener) { adapter.SetOnDeletedClickedListener(listener); }
    public void Remove(String item) { adapter.Remove(item); }
    public void Add(String item) { adapter.Add(item, 0); }
}
