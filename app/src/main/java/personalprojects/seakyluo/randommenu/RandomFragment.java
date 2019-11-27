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

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class RandomFragment extends Fragment {
    public static final String TAG = "RandomFragment";
    private AList<Food> food_pool = new AList<>(), filtered = new AList<>();
    private FoodCardFragment foodCardFragment;
    private static Random random = new Random();
    private AList<Food> Menu = new AList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        view.findViewById(R.id.check_button).setOnClickListener(v -> {
            Food food = NextFood();
            if (food == null) return;
            Menu.Add(food);
        });
        view.findViewById(R.id.cross_button).setOnClickListener(v -> {
            NextFood();
        });
        view.findViewById(R.id.refresh_button).setOnClickListener(v -> {
            food_pool.CopyFrom(Settings.settings.Foods.Filter(f -> !Menu.Contains(f)));
            NextFood();
        });
        getFragmentManager().beginTransaction().add(R.id.food_card_frame, foodCardFragment = new FoodCardFragment()).commit();
        Reset();
        if (!food_pool.IsEmpty()) foodCardFragment.LoadFood(RandomPop());
        return view;
    }

    private Food NextFood(){
        if (food_pool.IsEmpty()){
            Reset();
            Toast.makeText(getContext(), "Reaches End", Toast.LENGTH_SHORT).show();
        }
        return food_pool.IsEmpty() ? null : foodCardFragment.SetFood(RandomPop());
    }

    public Food RandomPop(){
        return food_pool.Pop(random.nextInt(food_pool.Count()));
    }

    private void Reset(){
        food_pool.CopyFrom(Settings.settings.Foods.Filter(f -> !Menu.Contains(f)));
    }
}
