package personalprojects.seakyluo.randommenu.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import personalprojects.seakyluo.randommenu.adapters.impl.StringListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.R;

public class StringListFragment extends Fragment {
    private StringListAdapter adapter = new StringListAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void setData(List<String> data){
        adapter.setData(data);
    }
    public void setItemClickedListener(DataItemClickedListener<String> listener) {
        adapter.setItemClickedListener(listener);
    }
    public void setItemDeletedListener(DataItemClickedListener<String> listener) {
        adapter.setItemDeletedListener(listener);
    }
    public void add(String item) {
        adapter.remove(item);
        adapter.add(item, 0);
    }
}
