package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;
import java.util.Set;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class RandomFragment extends Fragment {
    public static final String TAG = "RandomFragment";
    private AList<Food> food_pool = new AList<>(), filtered = new AList<>();
    private FoodCardFragment foodCardFragment;
    private AList<Food> Menu = new AList<>();
    private MenuDialog menuDialog = new MenuDialog();
    private FilterDialog filterDialog = new FilterDialog();
    private AList<Tag> preferred_tags = new AList<>(), excluded_tags = new AList<>();

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
        filterDialog.SetTagFilterListener((preferred, excluded) -> {
            preferred_tags.CopyFrom(preferred);
            excluded_tags.CopyFrom(excluded);
            filterDialog.dismiss();
            Reset();
            foodCardFragment.SetFood(NextFood());
        });
        filterDialog.SetOnResetListener(v -> {
            filterDialog.SetData(preferred_tags.Clear(), excluded_tags.Clear());
            Reset();
            foodCardFragment.SetFood(NextFood());
        });
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            filterDialog.SetData(preferred_tags, excluded_tags);
            filterDialog.showNow(getChildFragmentManager(), FilterDialog.TAG);
        });
        ImageButton menuButton = view.findViewById(R.id.menu_button);
        menuDialog.SetOnClearListener(button -> {
            food_pool.AddAll(Menu).Shuffle();
            Menu.Clear();
            menuDialog.SetHeaderText(String.format(getString(R.string.food_count), 0));
            menuDialog.Clear();
        });
        menuButton.setOnClickListener(v -> {
            menuDialog.SetHeaderText(String.format(getString(R.string.food_count), Menu.Count()));
            menuDialog.SetData(Menu);
            menuDialog.showNow(getChildFragmentManager(), MenuDialog.TAG);
        });
        getChildFragmentManager().beginTransaction().add(R.id.food_card_frame, foodCardFragment = new FoodCardFragment()).commit();
        Reset();
        if (!food_pool.IsEmpty()) foodCardFragment.LoadFood(food_pool.Pop(0));
        return view;
    }

    private Food NextFood(){
        if (food_pool.IsEmpty()){
            Reset();
            Toast.makeText(getContext(), getString(R.string.reshuffle), Toast.LENGTH_SHORT).show();
        }
        return food_pool.IsEmpty() ? null : foodCardFragment.SetFood(food_pool.Pop(0));
    }

    private AList<Food> Reset(){
        AList<Food> source = Settings.settings.Foods.Filter(f -> !Menu.Contains(f));
        if (!preferred_tags.IsEmpty()) source.Remove(f -> !preferred_tags.Any(f::HasTag));
        if (!excluded_tags.IsEmpty()) source.Remove(f -> excluded_tags.Any(f::HasTag));
        return food_pool.CopyFrom(source.Shuffle());
    }
}
