package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class FoodCardFragment extends Fragment {
    private TagsFragment tagsFragment;
    private TextView food_name;
    private ImageView food_image;
    private Food CurrentFood;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        getFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        setFood(CurrentFood);
        return view;
    }

    private void setFood(Food food){
        food_name.setText(food.Name);
        food_image.setImageBitmap(Helper.GetFoodBitmap(food));
        tagsFragment.SetData(food.GetTags(), false);
    }

    public void SetFood(Food food){ CurrentFood = food; }

    public void UpdateFood(Food food) {
        setFood(food);
    }
}
