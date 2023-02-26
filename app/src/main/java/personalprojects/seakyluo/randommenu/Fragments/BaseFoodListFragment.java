package personalprojects.seakyluo.randommenu.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.BaseFoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;

public abstract class BaseFoodListFragment<T extends BaseFoodListAdapter> extends Fragment {
    // 原数组，adapter.data可能为过滤后的数组
    protected AList<Food> data = new AList<>();
    protected T adapter;
    protected RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter.setContext(getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void setFoodClickedListener(DataItemClickedListener<Food> listener){ adapter.setFoodClickedListener(listener); }
    public void updateFood(Food before, Food after){
        int index = adapter.getData().indexOf(before);
        if (index > -1){
            adapter.set(after, index);
        }
    }
    public void clear() { this.data.clear(); adapter.clear(); }
    public void setData(AList<Food> data){ this.data.copyFrom(data); adapter.setData(data); }
    public void setData(List<Food> data){ this.data.copyFrom(data); adapter.setData(data); }
    public AList<Food> getData(){ return data; }
}
