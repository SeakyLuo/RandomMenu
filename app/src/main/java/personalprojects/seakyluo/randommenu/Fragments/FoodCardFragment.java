package personalprojects.seakyluo.randommenu.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.activities.impl.EditFoodActivity;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.interfaces.FoodEditedListener;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.activities.impl.MainActivity;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.services.SelfFoodService;
import personalprojects.seakyluo.randommenu.utils.ClipboardUtils;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;

public class FoodCardFragment extends Fragment {
    public static final String TAG = "FoodCardFragment", FOOD = "Food";
    private TagsFragment tagsFragment;
    private ImageViewerFragment imageViewerFragment;
    private TextView foodNameText, foodNoteFrontText, foodNoteBackText;
    private ImageView foodImage, likedImage;
    private ImageButton moreButton;
    @Getter
    @Setter
    private Food food;
    @Setter
    private Consumer<Food> foodEditedListener, foodLikedChangedListener;
    @Setter
    private boolean isTagClickable = true;
    private AnimatorSet flipInAnim, flipOutAnim;
    private boolean isBack = false; // 评论卡片方向
    private boolean isLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_card, container, false);
        foodNameText = view.findViewById(R.id.food_name);
        foodImage = view.findViewById(R.id.food_image);
        likedImage = view.findViewById(R.id.liked_image);
        foodNoteFrontText = view.findViewById(R.id.food_note_front);
        foodNoteBackText = view.findViewById(R.id.food_note_back);
        moreButton = view.findViewById(R.id.more_button);
        flipInAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_in);
        flipOutAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_out);

        FragmentManager fragmentManager = getChildFragmentManager();
        if (savedInstanceState == null)
            fragmentManager.beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).
                                               add(R.id.imageviewer_frame, imageViewerFragment = new ImageViewerFragment()).
                                               commit();
        else{
            tagsFragment = (TagsFragment) fragmentManager.getFragment(savedInstanceState, TagsFragment.TAG);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
            food = savedInstanceState.getParcelable(FOOD);
        }

        isLoaded = true;
        setFlipProperty();
        foodNameText.setOnLongClickListener(v -> {
            ClipboardUtils.copy(getContext(), food.getName());
            Toast.makeText(getContext(), R.string.name_copied, Toast.LENGTH_SHORT).show();
            return true;
        });
        tagsFragment.setSpanCount(1);
        tagsFragment.setTagClickedListener(this::tagClicked);
        moreButton.setOnClickListener(this::moreClicked);
        foodNoteFrontText.setOnClickListener(v -> { if (!StringUtils.isEmpty(food.getName())) flip(); });
        foodNoteBackText.setOnClickListener(v -> { if (!StringUtils.isEmpty(food.getNote())) flip(); });
        fillFood(food);
        return view;
    }

    private void moreClicked(View view){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), moreButton);
        helper.setOnDismissListener(() -> ObjectAnimator.ofFloat(view, "rotation", 180, 360).start());

        if (!food.hasImage()){
            helper.removeItems(R.id.save_image_item, R.id.share_item);
        }
        if (StringUtils.isEmpty(food.getNote())) helper.removeItems(R.id.more_item);
        helper.removeItems(food.getHideCount() == 0 ? R.id.show_food_item : R.id.hide_food_item);
        helper.removeItems(food.isFavorite() ? R.id.like_food_item : R.id.dislike_food_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.edit_food_item:
                    Intent editFoodIntent = new Intent(getContext(), EditFoodActivity.class);
                    editFoodIntent.putExtra(EditFoodActivity.FOOD, food);
                    editFoodIntent.putExtra(EditFoodActivity.IS_DRAFT, true);
                    startActivityForResult(editFoodIntent, ActivityCodeConstant.EDIT_FOOD);
                    return true;
                case R.id.save_image_item:
                    ImageUtils.saveImage(ImageUtils.getFoodBitmap(imageViewerFragment.getCurrentImage()), Helper.ImageFolder, ImageUtils.newImageFileName());
                    Toast.makeText(getContext(), R.string.save_image_msg, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.share_item:
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FileUtils.getFileUri(getContext(), ImageUtils.getImagePath(imageViewerFragment.getCurrentImage())));
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, String.format(getString(R.string.share_item), food.getName())));
                    return true;
                case R.id.like_food_item:
                    setFoodFavorite(food, true);
                    return true;
                case R.id.dislike_food_item:
                    setFoodFavorite(food, false);
                    return true;
                case R.id.show_food_item:
                    setFoodHideCount(0);
                    return true;
                case R.id.hide_food_item:
                    setFoodHideCount(food.getHideCount() + 3);
                    return true;
                case R.id.more_item:
                    flip();
                    return true;
            }
            return false;
        });
        ObjectAnimator.ofFloat(view, "rotation", 0, 180).start();
        helper.show();
    }
    
    private void setFoodFavorite(Food food, boolean isFavorite){
        food.setFavorite(isFavorite);
        showFoodFavorite(isFavorite);
        SelfFoodService.updateFood(food);
        if (foodLikedChangedListener != null) foodLikedChangedListener.accept(food);
    }
    
    private void setFoodHideCount(int hideCount){
        food.setHideCount(hideCount);
        setFoodNote(food);
        SelfFoodService.updateFood(food);
    }

    private void tagClicked(CustomAdapter<Tag>.CustomViewHolder viewHolder, Tag tag){
        if (!isTagClickable){
            return;
        }
        FragmentActivity currentActivity = getActivity();
        if (!(currentActivity instanceof MainActivity)){
            currentActivity.finish();
        }
        MainActivity activity = (MainActivity) getActivity();
        activity.showFragment(NavigationFragment.TAG);
        NavigationFragment navigationFragment = (NavigationFragment) activity.getCurrentFragment();
        navigationFragment.selectTagAndHighlight(tag);
    }

    private void setFlipProperty(){
        final int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        foodNoteFrontText.setCameraDistance(scale);
        foodNoteBackText.setCameraDistance(scale);
        foodNoteBackText.setMovementMethod(new ScrollingMovementMethod());
    }

    private void flip(){
        if (isBack = !isBack) {
            flipInAnim.setTarget(foodNoteBackText);
            flipOutAnim.setTarget(foodNoteFrontText);
        } else {
            flipInAnim.setTarget(foodNoteFrontText);
            flipOutAnim.setTarget(foodNoteBackText);
        }
        flipInAnim.start();
        flipOutAnim.start();
    }

    public void refresh() {
        fillFood(food = SelfFoodService.selectById(food.getId()));
    }

    public Food fillFood(Food food){
        if (food == null){
            return null;
        }
        this.food = food;
        if (!isLoaded){
            return food;
        }
        foodNameText.setText(food.getName());
        foodNoteBackText.setText(food.getNote());
        setFoodNote(food);
        foodImage.setVisibility(food.hasImage() ? View.GONE : View.VISIBLE);
        imageViewerFragment.setImages(food.getImages(), food.getCover());
        showFoodFavorite(food.isFavorite());
        tagsFragment.setData(food.getTags());
        return food;
    }

    public void showFoodFavorite(boolean favorite) { likedImage.setVisibility(favorite ? View.VISIBLE : View.GONE); }

    public void setFoodNote(Food food){
        String food_info = String.format(getString(R.string.created_at), food.formatDateAdded());
        int hideCount = food.getHideCount();
        if (hideCount > 0) food_info += "\n" + String.format(getString(R.string.hide_recent), hideCount);
        String note = food.getNote();
        if (StringUtils.isEmpty(note)){
            foodNoteFrontText.setText(food_info);
            foodNoteBackText.setText(note);
            foodNoteFrontText.setOnLongClickListener(null);
        } else {
            foodNoteFrontText.setText(note);
            foodNoteBackText.setText(food_info);
            foodNoteFrontText.setOnLongClickListener(v -> {
                ClipboardUtils.copy(getContext(), note);
                Toast.makeText(getContext(), R.string.note_copied, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        if (requestCode == ActivityCodeConstant.EDIT_FOOD){
            Food food = data.getParcelableExtra(EditFoodActivity.FOOD);
            if (food == null) return;
            fillFood(food);
            if (foodEditedListener != null) foodEditedListener.accept(food);
        }
        else if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT_FOOD){
            // TODO
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.putFragment(outState, TagsFragment.TAG, tagsFragment);
        fragmentManager.putFragment(outState, ImageViewerFragment.TAG, imageViewerFragment);
        outState.putParcelable(FOOD, food);
    }
}

