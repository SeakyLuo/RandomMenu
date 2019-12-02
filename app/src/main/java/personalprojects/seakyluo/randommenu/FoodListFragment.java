package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class FoodListFragment extends Fragment {
    private RecyclerView recyclerView;
    private FoodListAdapter adapter;
    private AList<Food> data = new AList<>();
    private OnDataItemClickedListener<Food> listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new FoodListAdapter();
        adapter.SetData(data);
        adapter.SetFoodClickedListener(listener);
        adapter.SetActivity(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void SetData(AList<Food> data){ this.data.CopyFrom(data); if (adapter != null) adapter.SetData(data); }

    public void SetFoodClickedListener(OnDataItemClickedListener<Food> listener){ this.listener = listener; if (adapter != null) adapter.SetFoodClickedListener(listener); }

    public void AttachItemTouchHelper(ItemTouchHelper helper){
        helper.attachToRecyclerView(recyclerView);
    }
}
