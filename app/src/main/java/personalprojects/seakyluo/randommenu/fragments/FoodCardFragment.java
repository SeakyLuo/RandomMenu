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
import android.util.DisplayMetrics;
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
import personalprojects.seakyluo.randommenu.activities.EditSelfMadeFoodActivity;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.RestaurantFoodDaoService;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.activities.MainActivity;
import personalprojects.seakyluo.randommenu.models.BaseFood;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.ClipboardUtils;
import personalprojects.seakyluo.randommenu.utils.DeviceUtils;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;

public class FoodCardFragment extends Fragment {
    public static final String TAG = "FoodCardFragment", FOOD = "Food";
    private static final int MAX_WIDTH = 960;
    private TagsFragment tagsFragment;
    private ImageViewerFragment imageViewerFragment;
    private TextView foodNameText, foodNoteFrontText, foodNoteBackText;
    private ImageView likedImage;
    private ImageButton moreButton;
    @Getter
    private BaseFood food;
    @Setter
    private Consumer<BaseFood> foodEditedListener, foodLikedChangedListener;
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
        likedImage = view.findViewById(R.id.liked_image);
        foodNoteFrontText = view.findViewById(R.id.food_note_front);
        foodNoteBackText = view.findViewById(R.id.food_note_back);
        moreButton = view.findViewById(R.id.more_button);
        flipInAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_in);
        flipOutAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flip_out);

        FragmentManager fragmentManager = getChildFragmentManager();
        if (savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .add(R.id.tags_frame, tagsFragment = new TagsFragment())
                    .commit();
            imageViewerFragment = (ImageViewerFragment) fragmentManager.findFragmentById(R.id.imageviewer_fragment);
        }
        else {
            tagsFragment = (TagsFragment) fragmentManager.getFragment(savedInstanceState, TagsFragment.TAG);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (DeviceUtils.isFoldableScreen(displayMetrics)) {
            ViewGroup.MarginLayoutParams cardLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            // imageLayoutParams.width获取到的是-1，所以得算出来
            int imageWidth = Math.min(MAX_WIDTH, displayMetrics.widthPixels - cardLayoutParams.leftMargin - cardLayoutParams.rightMargin);
            View imageViewerFragmentView = imageViewerFragment.getView();
            ViewGroup.LayoutParams imageLayoutParams = imageViewerFragmentView.getLayoutParams();
            imageLayoutParams.width = imageWidth;
            imageViewerFragmentView.setLayoutParams(imageLayoutParams);
            cardLayoutParams.width = imageWidth + cardLayoutParams.leftMargin + cardLayoutParams.rightMargin;
            view.setLayoutParams(cardLayoutParams);
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
        foodNoteFrontText.setMovementMethod(new ScrollingMovementMethod());
        foodNoteFrontText.setOnClickListener(v -> {
            if (StringUtils.isNotEmpty(foodNoteBackText.getText())){
                flip();
            }
        });
        foodNoteBackText.setMovementMethod(new ScrollingMovementMethod());
        foodNoteBackText.setOnClickListener(v -> {
            if (StringUtils.isNotEmpty(foodNoteFrontText.getText())){
                flip();
            }
        });
        fillFood(food);
        return view;
    }

    private void moreClicked(View view){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.food_card_menu, getContext(), moreButton);
        helper.setOnDismissListener(() -> ObjectAnimator.ofFloat(view, "rotation", 180, 360).start());

        if (!food.hasImage()){
            helper.removeItems(R.id.save_image_item, R.id.share_item);
        }
        if (food.getFoodClass() == FoodClass.RESTAURANT){
            helper.removeItems(R.id.show_food_item, R.id.hide_food_item, R.id.like_food_item, R.id.dislike_food_item, R.id.more_item);
        } else {
            SelfMadeFood selfMadeFood = food.asSelfMadeFood();
            if (StringUtils.isEmpty(food.getNote())) helper.removeItems(R.id.more_item);
            helper.removeItems(selfMadeFood.getHideCount() == 0 ? R.id.show_food_item : R.id.hide_food_item);
        }
        helper.removeItems(food.isFavorite() ? R.id.like_food_item : R.id.dislike_food_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.edit_food_item:
                    if (food.getFoodClass() == FoodClass.RESTAURANT){
                        Intent editFoodIntent = new Intent(getContext(), EditRestaurantFoodActivity.class);
                        editFoodIntent.putExtra(EditRestaurantFoodActivity.DATA, food.asRestaurantFood());
                        startActivityForResult(editFoodIntent, ActivityCodeConstant.EDIT_RESTAURANT_FOOD);
                    } else {
                        Intent editFoodIntent = new Intent(getContext(), EditSelfMadeFoodActivity.class);
                        editFoodIntent.putExtra(EditSelfMadeFoodActivity.FOOD_ID, food.asSelfMadeFood().getId());
                        editFoodIntent.putExtra(EditSelfMadeFoodActivity.IS_DRAFT, false);
                        startActivityForResult(editFoodIntent, ActivityCodeConstant.EDIT_SELF_FOOD);
                    }
                    return true;
                case R.id.save_image_item:
                    ImageUtils.saveImage(ImageUtils.getFoodBitmap(imageViewerFragment.getCurrentImage()), FileUtils.SAVED_IMAGE_FOLDER, ImageUtils.newImageFileName());
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
                    setFoodFavorite(true);
                    return true;
                case R.id.dislike_food_item:
                    setFoodFavorite(false);
                    return true;
                case R.id.show_food_item:
                    setFoodHideCount(0);
                    return true;
                case R.id.hide_food_item:
                    setFoodHideCount(food.asSelfMadeFood().getHideCount() + 3);
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
    
    private void setFoodFavorite(boolean isFavorite){
        food.setFavorite(isFavorite);
        showFoodFavorite(isFavorite);
        if (food.getFoodClass() == FoodClass.SELF_MADE){
            SelfMadeFood selfMadeFood = food.asSelfMadeFood();
            selfMadeFood.setFavorite(isFavorite);
            SelfMadeFoodService.updateFood(selfMadeFood);
            if (foodLikedChangedListener != null) foodLikedChangedListener.accept(food);
        }
    }
    
    private void setFoodHideCount(int hideCount){
        SelfMadeFood selfMadeFood = food.asSelfMadeFood();
        selfMadeFood.setHideCount(hideCount);
        setFoodNote(food);
        SelfMadeFoodService.updateFood(selfMadeFood);
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
        isBack = !isBack;
        if (isBack) {
            flipInAnim.setTarget(foodNoteBackText);
            flipOutAnim.setTarget(foodNoteFrontText);
        } else {
            flipInAnim.setTarget(foodNoteFrontText);
            flipOutAnim.setTarget(foodNoteBackText);
        }
        flipInAnim.start();
        flipOutAnim.start();
    }

    public BaseFood fillFood(SelfMadeFood food){
        if (food == null){
            return null;
        }
        return fillFood(BaseFood.from(food));
    }

    public BaseFood fillFood(RestaurantFoodVO food){
        if (food == null){
            return null;
        }
        return fillFood(BaseFood.from(food));
    }

    private BaseFood fillFood(BaseFood food){
        this.food = food;
        if (food == null){
            return null;
        }
        if (!isLoaded){
            return food;
        }
        foodNameText.setText(food.getName());
        foodNoteBackText.setText(food.getNote());
        setFoodNote(food);
        imageViewerFragment.setImages(food.getImages(), food.getCover());
        showFoodFavorite(food.isFavorite());
        tagsFragment.setData(food.getTags());
        return this.food;
    }

    public void showFoodFavorite(boolean favorite) { likedImage.setVisibility(favorite ? View.VISIBLE : View.GONE); }

    public void setFoodNote(BaseFood food){
        if (food.getFoodClass() == FoodClass.RESTAURANT){
            foodNoteFrontText.setText(food.getNote());
            foodNoteBackText.setText("");
            return;
        }
        SelfMadeFood selfMadeFood = food.asSelfMadeFood();
        String food_info = String.format(getString(R.string.created_at), selfMadeFood.formatDateAdded());
        int hideCount = selfMadeFood.getHideCount();
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
        if (requestCode == ActivityCodeConstant.EDIT_SELF_FOOD){
            SelfMadeFood food = data.getParcelableExtra(EditSelfMadeFoodActivity.DATA);
            if (food == null) return;
            BaseFood baseFood = fillFood(food);
            if (foodEditedListener != null) foodEditedListener.accept(baseFood);
        }
        else if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT_FOOD){
            RestaurantFoodVO food = data.getParcelableExtra(EditRestaurantFoodActivity.DATA);
            if (food == null) return;
            BaseFood baseFood = fillFood(food);
            RestaurantFoodDaoService.update(food);
            if (foodEditedListener != null) foodEditedListener.accept(baseFood);
        }
        ActivityUtils.spreadOnActivityResult(getActivity(), requestCode, resultCode, data);
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

