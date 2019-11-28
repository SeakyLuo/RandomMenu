package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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
    private FoodEditedListener foodEditedListener, foodLikedChangedListener;
    private TagClickedListener tagClickedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        getChildFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        more_button = view.findViewById(R.id.more_button);

        tagsFragment.SetTagClickedListener((viewHolder, tag) -> {
            FragmentActivity currentActivity = getActivity();
            if (!(currentActivity instanceof MainActivity)) currentActivity.finish();
            MainActivity activity = (MainActivity)getActivity();
            activity.ShowFragment(NavigationFragment.TAG);
            NavigationFragment navigationFragment = (NavigationFragment) activity.GetCurrentFragment();
            navigationFragment.SelectTag(tag);
            if (tagClickedListener != null) tagClickedListener.TagClicked(viewHolder, tag);
        });
        food_image.setOnClickListener(v -> {
            FullScreenImageActivity.image = Helper.GetFoodBitmap(food_image);
            startActivity(new Intent(getContext(), FullScreenImageActivity.class));
        });
        more_button.setOnClickListener(v -> {
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), more_button);
            helper.removeItem(CurrentFood.IsFavorite() ? R.id.like_food_item : R.id.dislike_food_item);
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                Food before = CurrentFood.Copy();
                switch (menuItem.getItemId()){
                    case R.id.edit_food_item:
                        Intent intent = new Intent(getContext(), EditFoodActivity.class);
                        intent.putExtra(EditFoodActivity.FOOD, CurrentFood);
                        startActivityForResult(intent, EDIT_FOOD);
                        return true;
                    case R.id.like_food_item:
                        CurrentFood.SetIsFavorite(true);
                        Settings.settings.SetFavorite(CurrentFood, true);
                        foodLikedChangedListener.FoodEdited(before, CurrentFood);
                        return true;
                    case R.id.dislike_food_item:
                        CurrentFood.SetIsFavorite(false);
                        Settings.settings.SetFavorite(CurrentFood, false);
                        foodLikedChangedListener.FoodEdited(before, CurrentFood);
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

    public void SetFoodEditedListener(FoodEditedListener listener) { this.foodEditedListener = listener; }
    public void SetTagClickedListener(TagClickedListener listener) { this.tagClickedListener = listener; }
    public void SetFoodLikedChangedListener(FoodEditedListener listener){ this.foodLikedChangedListener = listener; }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (data != null){
            Food newFood = data.getParcelableExtra(EditFoodActivity.FOOD);
            setFood(newFood);
            if (foodEditedListener != null) foodEditedListener.FoodEdited(CurrentFood, newFood);
            CurrentFood = newFood;
        }
    }
}

interface FoodEditedListener{
    void FoodEdited(Food before, Food after);
}
