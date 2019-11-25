package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Set;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class RandomFragment extends Fragment {
    public static final String TAG = "RandomFragment";
    private AList<Food> food_pool = new AList<>();
    private ImageButton check_button, cross_button;
    private FoodCardFragment foodCardFragment;
    private static Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        check_button = view.findViewById(R.id.check_button);
        cross_button = view.findViewById(R.id.cross_button);
        check_button.setOnClickListener(v -> {
        });
        cross_button.setOnClickListener(v -> {
            if (food_pool.Count() == 0) food_pool.CopyFrom(Settings.settings.Foods);
            if (!food_pool.IsEmpty()) foodCardFragment.UpdateFood(RandomPop());
        });
        getFragmentManager().beginTransaction().add(R.id.food_card_frame, foodCardFragment = new FoodCardFragment()).commit();
        food_pool.CopyFrom(Settings.settings.Foods);
        if (!food_pool.IsEmpty()) foodCardFragment.SetFood(RandomPop());
        return view;
    }

    public Food RandomPop(){
        return food_pool.Pop(random.nextInt(food_pool.Count()));
    }

}
