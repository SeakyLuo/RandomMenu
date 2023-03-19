package personalprojects.seakyluo.randommenu.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.FoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.CustomDataItemClickedListener;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class FoodListFragment extends BaseFoodListFragment<FoodListAdapter> {
    public static final String TAG = "FoodListFragment";
    @Setter
    private boolean selectable = false;
    @Setter
    private boolean removable = true;
    @Setter
    private Consumer<Food> foodRemovedListener;
    @Setter
    private Consumer<Food> foodAddedListener;
    @Setter
    private CustomDataItemClickedListener<Food, Boolean> foodSelectedListener;

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

    public void setSelectedFood(AList<Food> data){
        adapter.setSelectedFoods(data);
    }
    public AList<Food> getSelectedFoods(){
        return adapter.getSelectedFoods();
    }
    public void removeFood(Food food) {
        int removedIndex = adapter.indexOf(food);
        data.pop(removedIndex);
        adapter.pop(removedIndex);
    }
    public void unselectFood(String food){
        CustomAdapter<Food>.CustomViewHolder viewHolder = adapter.getViewHolders().first(vh -> vh.getData().getName().equals(food));
        adapter.setSelected(viewHolder, false);
}
    public void filter(String keyword){
        adapter.setData(data.find(f -> f.getName().contains(keyword)));
    }
    public void cancelFilter(){
        adapter.setData(data);
        recyclerView.smoothScrollToPosition(0);
    }
    public void setFoodClickedListener(DataItemClickedListener<Food> listener){
        adapter.setFoodClickedListener(listener);
    }
    public void setShowLikeImage(boolean showLikeImage) {
        adapter.setShowLikeImage(showLikeImage);
    }

    public void addSwipeControl(){
        SwipeToDeleteUtils.apply(recyclerView, getContext(), i -> {
            Food food = adapter.getDataAt(i);
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
        }, Food::getName);
    }
}
