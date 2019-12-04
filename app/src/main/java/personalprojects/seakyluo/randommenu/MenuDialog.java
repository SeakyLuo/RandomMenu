package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;

public class MenuDialog extends DialogFragment {
    private FoodListFragment fragment = new FoodListFragment();
    private Button clear_button;
    private View.OnClickListener clearListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_menu, container, false);
        clear_button = view.findViewById(R.id.clear_button);

        getFragmentManager().beginTransaction().add(R.id.food_list_frame, fragment).commit();
        clear_button.setOnClickListener(clearListener);
        return view;
    }

    public void SetOnClearListener(View.OnClickListener listener) { clearListener = listener; }
    public void SetData(AList<Food> data) { fragment.SetData(data); }
}
