package personalprojects.seakyluo.randommenu;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Interfaces.FoodEditedListener;
import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

import static android.app.Activity.RESULT_OK;

public class FoodCardFragment extends Fragment {
    private static final int EDIT_FOOD = 0;
    private TagsFragment tagsFragment;
    private TextView food_name, food_note, food_note_placeholder;
    private ImageView food_image;
    private ImageButton more_button;
    private Food CurrentFood;
    private FoodEditedListener foodEditedListener, foodLikedChangedListener;
    private OnDataItemClickedListener<Tag> tagClickedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        getChildFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        food_note = view.findViewById(R.id.food_note);
        food_note_placeholder = view.findViewById(R.id.food_note_placeholder);
        more_button = view.findViewById(R.id.more_button);

        tagsFragment.SetSpanCount(1);
        tagsFragment.SetTagClickedListener((viewHolder, tag) -> {
            FragmentActivity currentActivity = getActivity();
            if (!(currentActivity instanceof MainActivity)) currentActivity.finish();
            MainActivity activity = (MainActivity) getActivity();
            activity.ShowFragment(NavigationFragment.TAG);
            NavigationFragment navigationFragment = (NavigationFragment) activity.GetCurrentFragment();
            navigationFragment.SelectTag(tag);
            if (tagClickedListener != null) tagClickedListener.Click(viewHolder, tag);
        });
        food_image.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.IMAGE, CurrentFood.ImagePath);
            startActivity(intent);
        });
        food_note.setMovementMethod(new ScrollingMovementMethod());
        more_button.setOnClickListener(v -> {
            ObjectAnimator.ofFloat(v, "rotation", 0, 180).start();
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), more_button);
            if (Helper.IsNullOrEmpty(CurrentFood.ImagePath)) helper.removeItem(R.id.save_food_item);
            helper.removeItem(CurrentFood.IsFavorite() ? R.id.like_food_item : R.id.dislike_food_item);
            helper.setOnDismissListener(() -> ObjectAnimator.ofFloat(v, "rotation", 180, 360).start());
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                Food before = CurrentFood.Copy();
                switch (menuItem.getItemId()){
                    case R.id.edit_food_item:
                        Intent intent = new Intent(getContext(), EditFoodActivity.class);
                        intent.putExtra(EditFoodActivity.FOOD, CurrentFood);
                        startActivityForResult(intent, EDIT_FOOD);
                        return true;
                    case R.id.save_food_item:
                        Helper.SaveImage(Helper.GetFoodBitmap(CurrentFood.ImagePath), Helper.ImageFolder, Helper.NewImageFileName());
                        Toast.makeText(getContext(), getString(R.string.save_image), Toast.LENGTH_SHORT).show();
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
        CurrentFood = food;
        food_name.setText(food.Name);
        food_note.setText(food.Note);
        if (Helper.IsNullOrEmpty(food.Note)){
            food_note.setVisibility(View.GONE);
            food_note_placeholder.setVisibility(View.VISIBLE);
        }else{
            food_note.setVisibility(View.VISIBLE);
            food_note_placeholder.setVisibility(View.GONE);
        }
        Helper.LoadImage(Glide.with(this), food.ImagePath, food_image);
        tagsFragment.SetData(food.GetTags());
    }

    public void LoadFood(Food food){ CurrentFood = food; }

    public Food SetFood(Food food) {
        setFood(food);
        return food;
    }
    public Food GetFood() { return CurrentFood; }

    public void SetFoodEditedListener(FoodEditedListener listener) { this.foodEditedListener = listener; }
    public void SetTagClickedListener(OnDataItemClickedListener<Tag> listener) { this.tagClickedListener = listener; }
    public void SetFoodLikedChangedListener(FoodEditedListener listener){ this.foodLikedChangedListener = listener; }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        Food newFood = data.getParcelableExtra(EditFoodActivity.FOOD);
        setFood(newFood);
        if (foodEditedListener != null) foodEditedListener.FoodEdited(CurrentFood, newFood);
        CurrentFood = newFood;
    }
}

