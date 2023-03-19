package personalprojects.seakyluo.randommenu.dialogs;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.activities.impl.ChooseFoodActivity;
import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.R;

import static android.app.Activity.RESULT_CANCELED;

public class MenuDialog extends DialogFragment {
    public static final String TAG = "MenuDialog";
    private FoodListFragment fragment = new FoodListFragment();
    private Button clear_button, add_button;
    @Setter
    private View.OnClickListener clearListener;
    @Setter
    private Consumer<Food> foodRemovedListener;
    @Setter
    private Consumer<List<Food>> foodAddedListener;
    private TextView header_text;
    private String header;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_menu, container, false);
        add_button = view.findViewById(R.id.add_button);
        clear_button = view.findViewById(R.id.clear_button);
        header_text = view.findViewById(R.id.header_text);

        fragment.setFoodRemovedListener(data -> {
            if (foodRemovedListener != null) foodRemovedListener.accept(data);
            fragment.removeFood(data);
        });
        if (savedInstanceState == null){
            getChildFragmentManager().beginTransaction().add(R.id.food_list_frame, fragment).commit();
        }else{
            fragment = (FoodListFragment) getChildFragmentManager().getFragment(savedInstanceState, FoodListFragment.TAG);
        }
        add_button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChooseFoodActivity.class);
            intent.putExtra(ChooseFoodActivity.TAG, fragment.getData());
            startActivityForResult(intent, ChooseFoodActivity.CODE);
        });
        clear_button.setOnClickListener(clearListener);
        header_text.setText(header);
        return view;
    }

    public void setHeaderText(String text) {
        header = text;
        if (header_text != null){
            header_text.setText(header);
        }
    }

    public void setData(AList<Food> data) {
        fragment.setData(data);
    }

    public void clear() {
        fragment.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        List<Food> foods = data.getParcelableArrayListExtra(ChooseFoodActivity.TAG);
        fragment.setData(foods);
        foodAddedListener.accept(foods);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getChildFragmentManager().putFragment(outState, FoodListFragment.TAG, fragment);
    }
}
