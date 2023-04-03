package personalprojects.seakyluo.randommenu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.fragments.ChooseTagFragment;
import personalprojects.seakyluo.randommenu.fragments.ImageViewerFragment;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.FoodTagUtils;

public class EditSelfMadeFoodActivity extends AppCompatActivity {
    public static final String DATA = "Food", FOOD_ID = "FoodId", IS_DRAFT = "IsDraft";
    private EditText edit_food_name, edit_note;
    private SwitchCompat like_toggle;
    private ChooseTagFragment chooseTagFragment;
    private ImageViewerFragment imageViewerFragment;
    private SelfMadeFood currentFood;
    private boolean isDraft = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        ImageButton cancel_button = findViewById(R.id.cancel_button);
        ImageButton confirm_button = findViewById(R.id.confirm_button);
        Button deleteFoodButton = findViewById(R.id.delete_food_button);
        edit_food_name = findViewById(R.id.edit_food_name);
        edit_note = findViewById(R.id.edit_note);
        like_toggle = findViewById(R.id.like_toggle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            chooseTagFragment = (ChooseTagFragment) fragmentManager.findFragmentById(R.id.choose_tag_fragment);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.findFragmentById(R.id.imageviewer_fragment);
            Intent intent = getIntent();
            long foodId = intent.getLongExtra(FOOD_ID, -1);
            currentFood = foodId < 0 ? intent.getParcelableExtra(DATA) : SelfMadeFoodService.selectById(foodId);
            isDraft = intent.getBooleanExtra(IS_DRAFT, false);
        } else {
            chooseTagFragment = (ChooseTagFragment) fragmentManager.getFragment(savedInstanceState, ChooseTagFragment.TAG);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
            currentFood = savedInstanceState.getParcelable(DATA);
            isDraft = savedInstanceState.getBoolean(DATA);
        }
        imageViewerFragment.setEditable(true);
        setFood(currentFood);

        cancel_button.setOnClickListener(v -> onCancel());
        confirm_button.setOnClickListener(v -> onConfirm());
        deleteFoodButton.setVisibility(currentFood == null ? View.GONE : View.VISIBLE);
        deleteFoodButton.setOnClickListener(v -> deleteFood());
    }

    private void deleteFood(){
        AskYesNoDialog dialog = new AskYesNoDialog();
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        dialog.setMessage(R.string.ask_delete_food);
        dialog.setYesListener(view -> {
            if (isDraft) Settings.settings.FoodDraft = null;
            else SelfMadeFoodService.removeFood(currentFood);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void onCancel(){
        String food_name = edit_food_name.getText().toString(), note = edit_note.getText().toString();
        AList<Tag> tags = chooseTagFragment.getData();
        List<String> images = imageViewerFragment.getImages();
        boolean nameChanged = currentFood == null ? food_name.length() > 0 : !food_name.equals(currentFood.getName()),
                imageChanged = currentFood == null ? !images.isEmpty() : !images.equals(currentFood.getImages()),
                tagChanged = currentFood == null ? tags.size() > 0 : !tags.equals(currentFood.getTags()),
                noteChanged = currentFood == null ? note.length() > 0 : !note.equals(currentFood.getNote()),
                likeChanged = currentFood != null && like_toggle.isChecked() != currentFood.isFavorite();
        if (nameChanged || imageChanged || tagChanged || noteChanged || likeChanged){
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
            dialog.setMessage(R.string.save_as_draft);
            dialog.setYesListener(view -> {
                Settings.settings.FoodDraft = new SelfMadeFood(food_name, imageViewerFragment.getImages(), tags, note, like_toggle.isChecked(), imageViewerFragment.getCoverImage());
                finish();
            });
            dialog.setNoListener(view -> finish());
        } else {
            finish();
        }
    }

    private boolean isNewFood(){
        return currentFood == null || currentFood.getId() == 0;
    }

    private void onConfirm(){
        String foodName = getFoodName();
        if (foodName.length() == 0){
            Toast.makeText(this, R.string.empty_food_name, Toast.LENGTH_SHORT).show();
            return;
        }
        AList<Tag> tags = chooseTagFragment.getData();
        String note = getNote();

        // 草稿 or 新食物 or 改名成重名
        SelfMadeFood duplicate = SelfFoodDaoService.selectByName(foodName);
        if (duplicate != null && (isDraft || isNewFood() || !currentFood.getName().equals(foodName))){
            showDuplicateNameFoodDialog(duplicate);
            return;
        }

        if (tags.isEmpty()){
            if (Settings.settings.AutoTag){
                List<Tag> guessTags = FoodTagUtils.guessTags(foodName);
                chooseTagFragment.setData(guessTags);
                if (guessTags.size() > 0){
                    Toast.makeText(this, R.string.tag_auto_added, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.auto_tag_failed, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.at_least_one_tag, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        List<String> images = imageViewerFragment.getImages();
        String foodCover = imageViewerFragment.getCoverImage();
        SelfMadeFood food = new SelfMadeFood(foodName, images, tags, note, like_toggle.isChecked(), foodCover);
        if (isDraft){
            SelfMadeFoodService.addFood(food);
            Settings.settings.FoodDraft = null;
        }
        else if (isNewFood()){
            SelfMadeFoodService.addFood(food);
        }
        else {
            currentFood.setName(food.getName());
            currentFood.setImages(food.getImages());
            currentFood.setTags(food.getTags());
            currentFood.setNote(food.getNote());
            currentFood.setFavorite(food.isFavorite());
            currentFood.setCover(food.getCover());
            SelfMadeFoodService.updateFood(currentFood);
        }
        finishWithFood(food);
    }

    private void showDuplicateNameFoodDialog(SelfMadeFood existing){
        AskYesNoDialog dialog = new AskYesNoDialog();
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        dialog.setMessage(R.string.duplicate_food_merge);
        dialog.setYesListener(view -> {
            existing.getImages().addAll(imageViewerFragment.getImages());
            String foodCover = imageViewerFragment.getCoverImage();
            if (!StringUtils.isEmpty(foodCover)){
                existing.setCover(foodCover);
            }
            existing.setFavorite(existing.isFavorite() || like_toggle.isChecked());
            existing.addTags(chooseTagFragment.getData());
            String note = existing.getNote();
            if (!StringUtils.isBlank(note)){
                existing.setNote(note + "\n" + getNote());
            }
            SelfMadeFoodService.updateFood(existing);
            finishWithFood(existing);
        });
        dialog.setNoListener(view -> {
            Toast.makeText(this, R.string.food_exists, Toast.LENGTH_SHORT).show();
        });
    }

    private void setFood(SelfMadeFood food){
        if (food == null) return;
        edit_food_name.setText(food.getName());
        imageViewerFragment.setImages(food.getImages(), food.getCover());
        chooseTagFragment.setData(food.getTags());
        edit_note.setText(food.getNote());
        like_toggle.setChecked(food.isFavorite());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, ChooseTagFragment.TAG, chooseTagFragment);
        fragmentManager.putFragment(outState, ImageViewerFragment.TAG, imageViewerFragment);
        outState.putParcelable(DATA, new SelfMadeFood(getFoodName(), imageViewerFragment.getImages(), chooseTagFragment.getData(), getNote(), like_toggle.isChecked(), imageViewerFragment.getCoverImage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityUtils.spreadOnActivityResult(this, requestCode, resultCode, data);
    }

    private String getFoodName() {
        return edit_food_name.getText().toString().trim();
    }

    private String getNote() {
        return edit_note.getText().toString().trim();
    }

    private void finishWithFood(SelfMadeFood food){
        Intent intent = new Intent();
        intent.putExtra(DATA, food);
        setResult(RESULT_OK, intent);
        finish();
    }
}