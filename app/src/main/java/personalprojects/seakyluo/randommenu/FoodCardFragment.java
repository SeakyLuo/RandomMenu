package personalprojects.seakyluo.randommenu;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private TextView food_name, food_note_front, food_note_back;
    private ImageView food_image;
    private ImageButton more_button;
    private Food CurrentFood;
    private FoodEditedListener foodEditedListener, foodLikedChangedListener;
    private OnDataItemClickedListener<Tag> tagClickedListener;
    private AnimatorSet flip_in, flip_out;
    private boolean isBack = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        getChildFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        food_note_front = view.findViewById(R.id.food_note_front);
        food_note_back = view.findViewById(R.id.food_note_back);
        more_button = view.findViewById(R.id.more_button);
        flip_in = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_in);
        flip_out = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_out);

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
        food_note_back.setMovementMethod(new ScrollingMovementMethod());
        more_button.setOnClickListener(v -> {
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), more_button);
            if (Helper.IsNullOrEmpty(CurrentFood.ImagePath)) helper.removeItem(R.id.save_food_item);
            if (Helper.IsNullOrEmpty(CurrentFood.Note)) helper.removeItem(R.id.more_item);
            if (CurrentFood.HideCount == 0) helper.removeItem(R.id.show_food_item);
            else helper.removeItem(R.id.hide_food_item);
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
                        Toast.makeText(getContext(), getString(R.string.save_image_msg), Toast.LENGTH_SHORT).show();
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
                    case R.id.show_food_item:
                        CurrentFood.HideCount = 0;
                        SetFoodNote(CurrentFood);
                        Settings.settings.Foods.Find(CurrentFood).HideCount = CurrentFood.HideCount;
                        Helper.Save(getContext());
                        return true;
                    case R.id.hide_food_item:
                        CurrentFood.HideCount += 3;
                        SetFoodNote(CurrentFood);
                        Settings.settings.Foods.Find(CurrentFood).HideCount = CurrentFood.HideCount;
                        Helper.Save(getContext());
                        return true;
                    case R.id.more_item:
                        Flip();
                        return true;
                }
                return false;
            });
            ObjectAnimator.ofFloat(v, "rotation", 0, 180).start();
            helper.show();
        });
        food_note_front.setOnClickListener(v -> { if (!Helper.IsNullOrEmpty(CurrentFood.Note)) Flip(); });
        food_note_back.setOnClickListener(v -> { if (!Helper.IsNullOrEmpty(CurrentFood.Note)) Flip(); });
        if (CurrentFood != null) setFood(CurrentFood);
        return view;
    }

    private void Flip(){
        if (isBack = !isBack) {
            flip_in.setTarget(food_note_back);
            flip_out.setTarget(food_note_front);
            flip_in.start();
            flip_out.start();
        }else{
            flip_in.setTarget(food_note_front);
            flip_out.setTarget(food_note_back);
            flip_in.start();
            flip_out.start();
        }
    }

    private void setFood(Food food){
        CurrentFood = food;
        food_name.setText(food.Name);
        food_note_back.setText(food.Note);
        SetFoodNote(food);
        Helper.LoadImage(Glide.with(this), food.ImagePath, food_image);
        tagsFragment.SetData(food.GetTags());
    }

    public void SetFoodNote(Food food){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(food.GetDateAdded());
        String food_info = String.format(getString(R.string.created_at), new SimpleDateFormat("yyyy-MM-dd").format(date.getTime())) + "\n";
        if (food.HideCount > 0) food_info += String.format(getString(R.string.hide_recent), food.HideCount);
        if (Helper.IsNullOrEmpty(food.Note)){
            food_note_front.setText(food_info);
            food_note_back.setText(food.Note);
        }else{
            food_note_front.setText(food.Note);
            food_note_back.setText(food_info);
        }
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

