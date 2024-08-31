package personalprojects.seakyluo.randommenu.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.controls.EnhancedBottomSheetDialogFragment;
import personalprojects.seakyluo.randommenu.database.services.RestaurantFoodDaoService;
import personalprojects.seakyluo.randommenu.fragments.FoodCardFragment;
import personalprojects.seakyluo.randommenu.models.BaseFood;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;
import personalprojects.seakyluo.randommenu.utils.DeviceUtils;

public class FoodCardDialog extends EnhancedBottomSheetDialogFragment {
    public static final String TAG = "FoodCardDialog";
    private final FoodCardFragment foodCardFragment = new FoodCardFragment();
    @Setter
    private Long selfFoodId;
    @Setter
    private Long restaurantFoodId;

    public FoodCardDialog(){
        showCompatDialogOnFoldableScreen = true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (DeviceUtils.isFoldableScreen(getContext())){
            return new AppCompatDialog(getContext(), getTheme());
        } else {
            return new BottomSheetDialog(getContext(), getTheme());
        }
    }

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

    public void setFoodEditedListener(Consumer<BaseFood> listener) {
        foodCardFragment.setFoodEditedListener(listener);
    }

    public void setFoodLikedListener(Consumer<BaseFood> listener) {
        foodCardFragment.setFoodLikedChangedListener(listener);
    }

}
