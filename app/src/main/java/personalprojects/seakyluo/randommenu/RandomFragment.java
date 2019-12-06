package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.Random;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.FilterDialog;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class RandomFragment extends Fragment {
    public static final String TAG = "RandomFragment";
    private AList<Food> food_pool = new AList<>(), filtered = new AList<>();
    private FoodCardFragment foodCardFragment;
    private static Random random = new Random();
    private AList<Food> Menu = new AList<>();
    private MenuDialog menuDialog = new MenuDialog();
    private FilterDialog filterDialog = new FilterDialog();
//    private StackView stackView;
//    private CardStackAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        view.findViewById(R.id.check_button).setOnClickListener(v -> {
            Menu.Add(foodCardFragment.GetFood(), 0);
            NextFood();
        });
        view.findViewById(R.id.cross_button).setOnClickListener(v -> {
            NextFood();
        });
        view.findViewById(R.id.refresh_button).setOnClickListener(v -> {
            Reset();
            NextFood();
        });
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            filterDialog.showNow(getFragmentManager(), FilterDialog.TAG);
//            PopupWindow popupWindow = new PopupWindow(dialog.getView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            // Set popup window animation style.
//            popupWindow.setAnimationStyle(R.anim.push_up_in);
//            popupWindow.setFocusable(true);
//            popupWindow.setOutsideTouchable(true);
//            popupWindow.update();
//            popupWindow.showAsDropDown(filterButton, 1, 1);
        });
        ImageButton menuButton = view.findViewById(R.id.menu_button);
        menuDialog.SetOnClearListener(button -> {
            food_pool.AddAll(Menu).Shuffle();
            Menu.Clear();
            menuDialog.Clear();
        });
        menuButton.setOnClickListener(v -> {
            menuDialog.SetHeaderText("Count: " + Menu.Count());
            menuDialog.SetData(Menu);
            menuDialog.showNow(getFragmentManager(), MenuDialog.TAG);
//            PopupWindow popupWindow = new PopupWindow(menuDialog.getView(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            // Set popup window animation style.
//            popupWindow.setAnimationStyle(R.anim.push_up_in);
//            popupWindow.setFocusable(true);
//            popupWindow.setOutsideTouchable(true);
//            popupWindow.update();
//            popupWindow.showAsDropDown(menuButton, 1, 1);
        });
//        stackView = view.findViewById(R.id.card_stack_view);
//        adapter = new CardStackAdapter(getContext(), getFragmentManager(), Settings.settings.Foods);
//        stackView.setAdapter(adapter);
        getFragmentManager().beginTransaction().add(R.id.food_card_frame, foodCardFragment = new FoodCardFragment()).commit();
        Reset();
        if (!food_pool.IsEmpty()) foodCardFragment.LoadFood(RandomPop());
        return view;
    }

    private Food NextFood(){
//        if (adapter.data.IsEmpty()){
//            Reset();
//            Toast.makeText(getContext(), "Reaches End", Toast.LENGTH_SHORT).show();
//        }
//        if (adapter.data.IsEmpty()) return null;
//        stackView.showNext();
//        return adapter.data.Get(stackView.getTop());
        if (food_pool.IsEmpty()){
            Reset();
            Toast.makeText(getContext(), "Reaches End", Toast.LENGTH_SHORT).show();
        }
        return food_pool.IsEmpty() ? null : foodCardFragment.SetFood(RandomPop());
    }

    public Food RandomPop(){
        return food_pool.Pop(random.nextInt(food_pool.Count()));
    }

    private AList<Food> Reset(){
//        return adapter.data.CopyFrom(Settings.settings.Foods.Filter(f -> !Menu.Contains(f)));
        return food_pool.CopyFrom(Settings.settings.Foods.Filter(f -> !Menu.Contains(f)));
    }
}
