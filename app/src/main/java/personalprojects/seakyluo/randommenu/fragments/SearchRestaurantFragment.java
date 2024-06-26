package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.interfaces.SearchFragment;
import personalprojects.seakyluo.randommenu.models.MatchResult;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;

public class SearchRestaurantFragment extends Fragment implements SearchFragment<RestaurantVO> {
    @Setter
    @Getter
    private String name;
    protected RecyclerView recyclerView;
    private RestaurantAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        if (adapter == null){
            adapter = new RestaurantAdapter(getContext());
        } else {
            adapter.setContext(getContext());
        }
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void setKeyword(String keyword) {
        adapter.setKeyword(keyword);
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void setSearchResult(List<MatchResult<RestaurantVO>> data) {
        adapter.setData(data.stream()
                .sorted(Comparator.comparing(MatchResult<RestaurantVO>::getPoints).reversed())
                .map(MatchResult::getData)
                .collect(Collectors.toList()));
    }

    public void setData(List<RestaurantVO> data){
        if (adapter == null){
            adapter = new RestaurantAdapter(null);
        }
        adapter.setData(data);
    }

    public void updateRestaurant(RestaurantVO restaurant){
        if (restaurant == null){
            return;
        }
        int index = adapter.getData().indexOf(r -> r.getId() == restaurant.getId());
        if (index != -1){
            RestaurantVO current = adapter.getDataAt(index);
            if (current.getFoods() != null){
                restaurant.setFoods(current.getFoods());
            }
            adapter.set(restaurant, index);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityUtils.spreadOnActivityResult(getActivity(), requestCode, resultCode, data);
    }
}
