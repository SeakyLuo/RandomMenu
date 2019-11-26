package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

import static android.app.Activity.RESULT_OK;

public class FoodCardFragment extends Fragment {
    private static final int EDIT_FOOD = 0;
    private TagsFragment tagsFragment;
    private TextView food_name;
    private ImageView food_image;
    private ImageButton more_button;
    private Food CurrentFood;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        getChildFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        more_button = view.findViewById(R.id.more_button);

        food_image.setOnClickListener(v -> {
            FullScreenImageActivity.image = Helper.GetFoodBitmap(food_image);
            startActivity(new Intent(getContext(), FullScreenImageActivity.class));
        });
        more_button.setOnClickListener(v -> {
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), more_button);
            helper.removeItem(CurrentFood.IsFavorite() ? R.id.like_food_item : R.id.dislike_food_item);
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                switch (menuItem.getItemId()){
                    case R.id.edit_food_item:
                        Intent intent = new Intent(getContext(), EditFoodActivity.class);
                        intent.putExtra(EditFoodActivity.FOOD, CurrentFood);
                        startActivityForResult(intent, EDIT_FOOD);
                        return true;
                    case R.id.like_food_item:
                        CurrentFood.SetIsFavorite(true);
                        return true;
                    case R.id.dislike_food_item:
                        CurrentFood.SetIsFavorite(false);
                        return true;
                }
                return false;
            });
            helper.show();
        });
        if (CurrentFood != null) setFood(CurrentFood);
        return view;
    }

    private void setFood(Food food){
        food_name.setText(food.Name);
        food_image.setImageBitmap(Helper.GetFoodBitmap(food));
        tagsFragment.SetData(food.GetTags(), false);
    }

    public void LoadFood(Food food){ CurrentFood = food; }

    public Food SetFood(Food food) {
        setFood(food);
        return food;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (data != null)
            setFood(CurrentFood = data.getParcelableExtra(EditFoodActivity.FOOD));
    }
}
