package personalprojects.seakyluo.randommenu;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
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
    public static final String TAG = "FoodCardFragment", FOOD = "Food";
    private static final int EDIT_FOOD = 0;
    private TagsFragment tagsFragment;
    private ImageViewerFragment imageViewerFragment;
    private TextView food_name, food_note_front, food_note_back;
    private ImageView food_image, liked_image;
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
        food_name = view.findViewById(R.id.food_name);
        food_image = view.findViewById(R.id.food_image);
        liked_image = view.findViewById(R.id.liked_image);
        food_note_front = view.findViewById(R.id.food_note_front);
        food_note_back = view.findViewById(R.id.food_note_back);
        more_button = view.findViewById(R.id.more_button);
        flip_in = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_in);
        flip_out = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_out);
        FragmentManager fragmentManager = getChildFragmentManager();
        if (savedInstanceState == null)
            fragmentManager.beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).
                                               add(R.id.imageviewer_frame, imageViewerFragment = new ImageViewerFragment()).
                                               commit();
        else{
            tagsFragment = (TagsFragment) fragmentManager.getFragment(savedInstanceState, TagsFragment.TAG);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
            CurrentFood = savedInstanceState.getParcelable(FOOD);
        }

        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        food_note_front.setCameraDistance(scale);
        food_note_back.setCameraDistance(scale);

        food_name.setOnLongClickListener(v -> {
            Helper.CopyToClipboard(getContext(), CurrentFood.Name);
            Toast.makeText(getContext(), R.string.name_copied, Toast.LENGTH_SHORT).show();
            return true;
        });
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
        food_note_back.setMovementMethod(new ScrollingMovementMethod());
        more_button.setOnClickListener(v -> {
            final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), more_button);
            if (!CurrentFood.HasImage()) helper.removeItems(R.id.save_image_item, R.id.share_item);
            else if (CurrentFood.Images.Count() == 1) helper.removeItems(R.id.save_all_images_item);
            if (Helper.IsNullOrEmpty(CurrentFood.Note)) helper.removeItems(R.id.more_item);
            if (CurrentFood.HideCount == 0) helper.removeItems(R.id.show_food_item);
            else helper.removeItems(R.id.hide_food_item);
            helper.removeItems(CurrentFood.IsFavorite() ? R.id.like_food_item : R.id.dislike_food_item);
            helper.setOnDismissListener(() -> ObjectAnimator.ofFloat(v, "rotation", 180, 360).start());
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                Food before = CurrentFood.Copy();
                switch (menuItem.getItemId()){
                    case R.id.edit_food_item:
                        Intent editFoodIntent = new Intent(getContext(), EditFoodActivity.class);
                        editFoodIntent.putExtra(EditFoodActivity.FOOD, CurrentFood);
                        editFoodIntent.putExtra(EditFoodActivity.DELETE, true);
                        startActivityForResult(editFoodIntent, EDIT_FOOD);
                        return true;
                    case R.id.save_image_item:
                        Helper.SaveImage(Helper.GetFoodBitmap(imageViewerFragment.getCurrentImage()), Helper.SaveImageFolder, Helper.NewImageFileName());
                        Toast.makeText(getContext(), R.string.save_image_msg, Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.save_all_images_item:
                        CurrentFood.Images.ForEach(image -> Helper.SaveImage(Helper.GetFoodBitmap(image), Helper.SaveImageFolder, Helper.NewImageFileName()));
                        Toast.makeText(getContext(), String.format(getString(R.string.count_images_saved), CurrentFood.Images.Count()), Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.share_item:
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Helper.GetFileUri(getContext(), imageViewerFragment.getCurrentImage()));
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, String.format(getString(R.string.share_item), before.Name)));
                        return true;
                    case R.id.like_food_item:
                        SetFoodFavorite(CurrentFood.SetIsFavorite(true));
                        Settings.settings.SetFavorite(CurrentFood, true);
                        if (foodLikedChangedListener != null) foodLikedChangedListener.FoodEdited(before, CurrentFood);
                        return true;
                    case R.id.dislike_food_item:
                        SetFoodFavorite(CurrentFood.SetIsFavorite(false));
                        Settings.settings.SetFavorite(CurrentFood, false);
                        if (foodLikedChangedListener != null) foodLikedChangedListener.FoodEdited(before, CurrentFood);
                        return true;
                    case R.id.show_food_item:
                        CurrentFood.HideCount = 0;
                        SetFoodNote(CurrentFood);
                        Settings.settings.Foods.First(CurrentFood).HideCount = CurrentFood.HideCount;
                        Helper.Save();
                        return true;
                    case R.id.hide_food_item:
                        CurrentFood.HideCount += 3;
                        SetFoodNote(CurrentFood);
                        Settings.settings.Foods.First(CurrentFood).HideCount = CurrentFood.HideCount;
                        Helper.Save();
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

    public void Refresh() {
        setFood(CurrentFood = Settings.settings.Foods.First(CurrentFood));
    }

    private void setFood(Food food){
        CurrentFood = food;
        food_name.setText(food.Name);
        food_note_back.setText(food.Note);
        SetFoodNote(food);
        food_image.setVisibility(food.HasImage() ? View.GONE : View.VISIBLE);
        imageViewerFragment.setImages(food.Images);
        SetFoodFavorite(food.IsFavorite());
        tagsFragment.SetData(food.GetTags());
    }

    public void SetFoodFavorite(boolean favorite) { liked_image.setVisibility(favorite ? View.VISIBLE : View.GONE); }

    public void SetFoodNote(Food food){
        String food_info = String.format(getString(R.string.created_at), food.GetDateAddedString()) + "\n";
        if (food.HideCount > 0) food_info += String.format(getString(R.string.hide_recent), food.HideCount);
        if (Helper.IsNullOrEmpty(food.Note)){
            food_note_front.setText(food_info);
            food_note_back.setText(food.Note);
            food_note_front.setOnLongClickListener(null);
        }else{
            food_note_front.setText(food.Note);
            food_note_back.setText(food_info);
            food_note_front.setOnLongClickListener(v -> {
                Helper.CopyToClipboard(getContext(), food.Note);
                Toast.makeText(getContext(), R.string.note_copied, Toast.LENGTH_SHORT).show();
                return true;
            });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.putFragment(outState, TagsFragment.TAG, tagsFragment);
        fragmentManager.putFragment(outState, ImageViewerFragment.TAG, imageViewerFragment);
        outState.putParcelable(FOOD, CurrentFood);
    }
}

