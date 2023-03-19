package personalprojects.seakyluo.randommenu.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.FoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.CustomDataItemClickedListener;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.MatchFood;
import personalprojects.seakyluo.randommenu.utils.SearchUtils;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class FoodListFragment extends BaseFoodListFragment<FoodListAdapter> {
    public static final String TAG = "FoodListFragment";
    @Setter
    private boolean selectable = false;
    @Setter
    private boolean removable = true;
    @Setter
    private Consumer<SelfFood> foodRemovedListener;
    @Setter
    private Consumer<SelfFood> foodAddedListener;
    @Setter
    private CustomDataItemClickedListener<SelfFood, Boolean> foodSelectedListener;

    public FoodListFragment(){
        adapter = new FoodListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adapter.setSelectable(selectable);
        adapter.setSelectionChangedListener(foodSelectedListener);
        if (removable) addSwipeControl();
        return view;
    }

    public void setSelectedFood(AList<SelfFood> data){
        adapter.setSelectedFoods(data);
    }
    public AList<SelfFood> getSelectedFoods(){
        return adapter.getSelectedFoods();
    }
    public void removeFood(SelfFood food) {
        int removedIndex = adapter.indexOf(food);
        data.pop(removedIndex);
        adapter.pop(removedIndex);
    }
    public void unselectFood(String food){
        CustomAdapter<SelfFood>.CustomViewHolder viewHolder = adapter.getViewHolders().first(vh -> vh.getData().getName().equals(food));
        adapter.setSelected(viewHolder, false);
}
    public void filter(String keyword){
        adapter.setData(data.stream()
                .map(f -> SearchUtils.evalFood(f, keyword))
                .sorted(Comparator.comparing(MatchFood::getPointsWithBonus).reversed())
                .filter(i -> i.getPoints() > 0)
                .map(MatchFood::getFood)
                .collect(Collectors.toList()));
    }
    public void cancelFilter(){
        adapter.setData(data);
        recyclerView.smoothScrollToPosition(0);
    }
    public void setFoodClickedListener(DataItemClickedListener<SelfFood> listener){
        adapter.setFoodClickedListener(listener);
    }
    public void setShowLikeImage(boolean showLikeImage) {
        adapter.setShowLikeImage(showLikeImage);
    }

    public void addSwipeControl(){
        SwipeToDeleteUtils.apply(recyclerView, getContext(), i -> {
            SelfFood food = adapter.getDataAt(i);
            if (foodRemovedListener != null){
                foodRemovedListener.accept(food);
            }
            return food;
        }, (i, food) -> {
            adapter.add(food, i);
            adapter.notifyItemInserted(i);
            if (foodAddedListener != null){
                foodAddedListener.accept(food);
            }
        }, SelfFood::getName);
    }
}
