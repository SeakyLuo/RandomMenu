package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.fragments.FoodCardFragment;
import personalprojects.seakyluo.randommenu.interfaces.FoodEditedListener;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.services.SelfFoodService;

public class FoodCardDialog extends DialogFragment {
    public static final String TAG = "FoodCardDialog";
    private FoodCardFragment foodCardFragment = new FoodCardFragment();
    @Setter
    private Food food;
    @Setter
    private Long foodId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_food_card, container,false);
        getChildFragmentManager().beginTransaction().add(R.id.dialog_food_card_frame, foodCardFragment).commit();
        if (foodId != null){
            food = SelfFoodService.selectById(foodId);
        }
        foodCardFragment.setFood(food);
        foodCardFragment.setTagClickable(false);
        return view;
    }

    public void setFoodEditedListener(Consumer<Food> listener) {
        foodCardFragment.setFoodEditedListener(listener);
    }
    public void setFoodLikedListener(Consumer<Food> listener) {
        foodCardFragment.setFoodLikedChangedListener(listener);
    }
}
