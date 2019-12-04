package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personalprojects.seakyluo.randommenu.FoodCardFragment;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.R;

public class FoodCardDialog extends DialogFragment {
    private FoodCardFragment foodCardFragment = new FoodCardFragment();
    private Food CurrentFood;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_food_card, container,false);
        getChildFragmentManager().beginTransaction().add(R.id.dialog_food_card_frame, foodCardFragment).commit();
        foodCardFragment.LoadFood(CurrentFood);
        foodCardFragment.SetTagClickedListener((viewHolder, tag) -> {
//            dismiss();
        });
        return view;
    }

    public void SetFood(Food food){ CurrentFood = food; }
    public void SetFoodEditedListener(FoodEditedListener listener) { foodCardFragment.SetFoodEditedListener(listener); }
}
