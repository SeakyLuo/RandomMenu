package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.database.services.RestaurantFoodDaoService;
import personalprojects.seakyluo.randommenu.fragments.FoodCardFragment;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;

public class FoodCardDialog extends BottomSheetDialogFragment {
    public static final String TAG = "FoodCardDialog";
    private FoodCardFragment foodCardFragment = new FoodCardFragment();
    @Setter
    private Long selfFoodId;
    @Setter
    private Long restaurantFoodId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_food_card, container,false);
        getChildFragmentManager().beginTransaction().add(R.id.dialog_food_card_frame, foodCardFragment).commit();
        if (selfFoodId == null){
            foodCardFragment.fillFood(RestaurantFoodDaoService.selectById(restaurantFoodId));
        } else {
            foodCardFragment.fillFood(SelfMadeFoodService.selectById(selfFoodId));
        }
        foodCardFragment.setTagClickable(false);
        return view;
    }

    public void setFoodEditedListener(Consumer<SelfMadeFood> listener) {
        foodCardFragment.setFoodEditedListener(listener);
    }

    public void setFoodLikedListener(Consumer<SelfMadeFood> listener) {
        foodCardFragment.setFoodLikedChangedListener(listener);
    }
}
