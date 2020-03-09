package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

import static android.app.Activity.RESULT_CANCELED;

public class MenuDialog extends DialogFragment {
    public static final String TAG = "MenuDialog";
    private FoodListFragment fragment = new FoodListFragment();
    private Button clear_button, add_button;
    private View.OnClickListener addListener, clearListener;
    private OnDataItemClickedListener<Food> foodRemovedListener;
    private TextView header_text;
    private String header;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_menu, container, false);
        add_button = view.findViewById(R.id.add_button);
        clear_button = view.findViewById(R.id.clear_button);
        header_text = view.findViewById(R.id.header_text);

        fragment.SetFoodRemovedListener((viewHolder, data) -> {
            if (foodRemovedListener != null) foodRemovedListener.Click(viewHolder, data);
            fragment.RemoveFood(data);
        });
        getChildFragmentManager().beginTransaction().add(R.id.food_list_frame, fragment).commit();
        add_button.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), ChooseFoodActivity.class), ChooseFoodActivity.CODE);
        });
        clear_button.setOnClickListener(clearListener);
        header_text.setText(header);
        return view;
    }

    public void SetHeaderText(String text) { header = text; if (header_text != null) header_text.setText(header); }
    public void SetOnClearListener(View.OnClickListener listener) { clearListener = listener; if (clear_button != null) clear_button.setOnClickListener(clearListener); }
    public void SetFoodRemovedListener(OnDataItemClickedListener<Food> listener) { foodRemovedListener = listener; }
    public void SetData(AList<Food> data) { fragment.SetData(data); }
    public void Clear() { fragment.Clear(); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        AList<Food> foods = new AList<>();
        for (String name: data.getStringArrayExtra(ChooseFoodActivity.TAG)){
            foods.Add(Settings.settings.Foods.First(f -> f.Name.equals(name)));
        }
    }
}
