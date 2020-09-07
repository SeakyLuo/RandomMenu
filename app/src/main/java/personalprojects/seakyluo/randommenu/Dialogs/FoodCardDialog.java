package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personalprojects.seakyluo.randommenu.fragments.FoodCardFragment;
import personalprojects.seakyluo.randommenu.interfaces.FoodEditedListener;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.R;

public class FoodCardDialog extends DialogFragment {
    private FoodCardFragment foodCardFragment = new FoodCardFragment();
    private Food CurrentFood;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_food_card, container,false);
        getChildFragmentManager().beginTransaction().add(R.id.dialog_food_card_frame, foodCardFragment).commit();
        foodCardFragment.loadFood(CurrentFood);
        foodCardFragment.setTagClickedListener(((viewHolder, data) -> {}));
        return view;
    }

    public void setFood(Food food){ CurrentFood = food; }
    public void setFoodEditedListener(FoodEditedListener listener) { foodCardFragment.setFoodEditedListener(listener); }
    public void setFoodLikedListener(FoodEditedListener listener) { foodCardFragment.setFoodLikedChangedListener(listener); }
}
