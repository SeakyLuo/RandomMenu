package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodCardFragment extends Fragment {
    private TagFragment tagFragment;
    private TextView food_name;
    private ImageView food_image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        getFragmentManager().beginTransaction().add(R.id.tags_frame, tagFragment = new TagFragment()).commit();
        return view;
    }

    public void SetFood(Food food){
        food_name.setText(food.Name);
        food_image.setImageBitmap(Helper.GetFoodBitmap(food));
        tagFragment.SetData(food.GetTags(), false);
    }
}
