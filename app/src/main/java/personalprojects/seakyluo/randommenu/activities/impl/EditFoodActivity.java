package personalprojects.seakyluo.randommenu.activities.impl;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.fragments.ChooseTagFragment;
import personalprojects.seakyluo.randommenu.fragments.ImageViewerFragment;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.PermissionUtils;

public class EditFoodActivity extends AppCompatActivity {
    public static final String FOOD = "Food", IS_DRAFT = "IsDraft";
    private ImageButton camera_button;
    private EditText edit_food_name, edit_note;
    private ImageView food_image;
    private Switch like_toggle;
    private ChooseTagFragment chooseTagFragment;
    private ImageViewerFragment imageViewerFragment;
    private Food currentFood;
    private Uri camera_image_uri, crop_image_uri;
    private AList<String> images = new AList<>(), sources = new AList<>();
    private boolean isDraft = false;
    private String food_cover = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        ImageButton cancel_button = findViewById(R.id.cancel_button);
        ImageButton confirm_button = findViewById(R.id.confirm_button);
        Button delete_food_button = findViewById(R.id.delete_food_button);
        camera_button = findViewById(R.id.camera_button);
        edit_food_name = findViewById(R.id.edit_food_name);
        edit_note = findViewById(R.id.edit_note);
        food_image = findViewById(R.id.food_image);
        like_toggle = findViewById(R.id.like_toggle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            chooseTagFragment = (ChooseTagFragment) fragmentManager.findFragmentById(R.id.choose_tag_fragment);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.findFragmentById(R.id.imageviewer_fragment);
            currentFood = getIntent().getParcelableExtra(FOOD);
            isDraft = getIntent().getBooleanExtra(IS_DRAFT, false);
        }else{
            chooseTagFragment = (ChooseTagFragment) fragmentManager.getFragment(savedInstanceState, ChooseTagFragment.TAG);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
            currentFood = savedInstanceState.getParcelable(FOOD);
            isDraft = savedInstanceState.getBoolean(FOOD);
        }
        setFood(currentFood);

        cancel_button.setOnClickListener(v -> onCancel());
        confirm_button.setOnClickListener(v -> onConfirm());
        camera_button.setOnClickListener(v -> {
            if (PermissionUtils.checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityCodeConstant.WRITE_STORAGE)){
                showMenuFlyout();
            }
        });
        delete_food_button.setVisibility(currentFood == null ? View.GONE : View.VISIBLE);
        delete_food_button.setOnClickListener(v -> deleteFood());
    }

    private void deleteFood(){
        AskYesNoDialog dialog = new AskYesNoDialog();
        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        dialog.setMessage(R.string.ask_delete_food);
        dialog.setYesListener(view -> {
            if (isDraft) Settings.settings.FoodDraft = null;
            else Settings.settings.removeFood(currentFood);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void onCancel(){
        String food_name = edit_food_name.getText().toString(), note = edit_note.getText().toString();
        AList<Tag> tags = chooseTagFragment.getData();
        boolean nameChanged = currentFood == null ? food_name.length() > 0 : !food_name.equals(currentFood.Name),
                imageChanged = currentFood == null ? images.size() > 0 : !images.equals(currentFood.Images),
                tagChanged = currentFood == null ? tags.size() > 0 : !tags.equals(currentFood.getTags()),
                noteChanged = currentFood == null ? note.length() > 0 : !note.equals(currentFood.Note),
                likeChanged = currentFood != null && like_toggle.isChecked() != currentFood.isFavorite();
        if (nameChanged || imageChanged || tagChanged || noteChanged || likeChanged){
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
            dialog.setMessage(R.string.save_as_draft);
            dialog.setYesListener(view -> {
                Settings.settings.FoodDraft = new Food(food_name, images, tags, note, like_toggle.isChecked(), food_cover);
                finish();
            });
            dialog.setNoListener(view -> finish());
        }else{
            finish();
        }
    }

    private void onConfirm(){
        String food_name = getFoodName();
        if (food_name.length() == 0){
            Toast.makeText(this, R.string.empty_food_name, Toast.LENGTH_SHORT).show();
            return;
        }
        // 非草稿、重名、新菜
        if (!isDraft && Settings.settings.Foods.any(f -> f.Name.equals(food_name)) && (currentFood == null || !currentFood.Name.equals(food_name))){
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
            dialog.setMessage(R.string.duplicate_food_merge);
            dialog.setYesListener(view -> {
                int index = Settings.settings.Foods.indexOf(f -> f.Name.equals(food_name));
                Food food = Settings.settings.Foods.get(index);
                food.Images.addAll(images);
                if (!StringUtils.isEmpty(food_cover)){
                    food.setCover(food_cover);
                }
                food.setIsFavorite(food.isFavorite() || like_toggle.isChecked());
                food.AddTags(chooseTagFragment.getData());
                if (!StringUtils.isBlank(food.Note)){
                    food.Note = food.Note + '\n' + getNote();
                }
                Settings.settings.Foods.move(index, 0);
                finishWithFood(food);
            });
            dialog.setNoListener(view -> {
                Toast.makeText(this, R.string.food_exists, Toast.LENGTH_SHORT).show();
            });
            return;
        }
        AList<Tag> tags = chooseTagFragment.getData();
        if (tags.isEmpty()){
            if (Settings.settings.AutoTag){
                List<Tag> guessTags = Helper.guessTags(food_name);
                chooseTagFragment.setData(guessTags);
                if (guessTags.size() > 0){
                    Toast.makeText(this, R.string.tag_auto_added, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, R.string.auto_tag_failed, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, R.string.at_least_one_tag, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        String note = getNote();
        Food food = new Food(food_name, images, tags, note, like_toggle.isChecked(), food_cover);
        if (Food.IsIncomplete(currentFood)) Settings.settings.addFood(food);
        else if (isDraft && !Settings.settings.Foods.any(f -> f.Name.equals(food_name))){
            Settings.settings.addFood(food);
            Settings.settings.FoodDraft = null;
        }else{
            Settings.settings.updateFood(currentFood, food);
        }
        finishWithFood(food);
    }

    private void showMenuFlyout(){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, this, camera_button);
        if (images.isEmpty()) helper.removeItems(R.id.edit_image_item, R.id.remove_image_item);
        if (images.size() < 2 || imageViewerFragment.getCurrentImage().equals(food_cover)) helper.removeItems(R.id.set_cover_item);
        if (images.isEmpty() || imageViewerFragment.getCurrent() == 0) helper.removeItems(R.id.move_to_first_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    crop_image_uri = ImageUtils.openCamera(this);
                    return crop_image_uri != null;
                case R.id.open_gallery_item:
                    ImageUtils.openGallery(this);
                    return true;
                case R.id.edit_image_item:
                    crop_image_uri = ImageUtils.cropImage(this, getCurrentImage());
                    return crop_image_uri != null;
                case R.id.remove_image_item:
                    String image = images.pop(imageViewerFragment.removeCurrentImage());
                    if (image.equals(food_cover)) food_cover = images.isEmpty() ? "" : images.first();
                    if (images.isEmpty()) food_image.setVisibility(View.VISIBLE);
                    return true;
                case R.id.set_cover_item:
                    food_cover = imageViewerFragment.getCurrentImage();
                    return true;
                case R.id.move_to_first_item:
                    images.move(imageViewerFragment.getCurrent(), 0);
                    imageViewerFragment.Move(imageViewerFragment.getCurrent(), 0);
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private void setFood(Food food){
        if (food == null) return;
        edit_food_name.setText(food.Name);
        food_image.setVisibility(food.hasImage() ? View.GONE : View.VISIBLE);
        imageViewerFragment.setImages(images.copyFrom(food.Images), food_cover = food.getCover());
        sources.copyFrom(new AList<>("", food.Images.size()));
        chooseTagFragment.setData(food.getTags());
        edit_note.setText(food.Note);
        like_toggle.setChecked(food.isFavorite());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            return;
        switch (requestCode){
            case ActivityCodeConstant.WRITE_STORAGE:
                showMenuFlyout();
                break;
            case ActivityCodeConstant.CAMERA:
                ImageUtils.openCamera(this);
                break;
            case ActivityCodeConstant.READ_EXTERNAL_STORAGE_CODE:
                ImageUtils.openGallery(this);
                break;
        }
    }

    private String getCurrentImage() {
        return ImageUtils.getImagePath(images.get(imageViewerFragment.getCurrent()));
    }

    private boolean addImage(Bitmap image, String filename){
        boolean success = ImageUtils.saveImage(image, Helper.ImageFolder, filename);
        if (success) images.with(imageViewerFragment.addImage(filename), 0);
        return success;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bitmap image;
        String filename = "";
        switch (requestCode){
            case ActivityCodeConstant.CAMERA:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), camera_image_uri);
                    if (addImage(image, filename = camera_image_uri.getPath()))
                        sources.with(filename, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case ActivityCodeConstant.GALLERY:
                try {
                    Uri uri;
                    int index;
                    ClipData clipData = data.getClipData();
                    if (clipData == null) {
                        uri = data.getData();
                        index = sources.indexOf(uri.getPath());
                        // Avoid re-adding images
                        if (index > -1){
                            images.move(index, 0);
                            sources.move(index, 0);
                            imageViewerFragment.moveImage(index, 0);
                        }else if (addImage(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), ImageUtils.newImageFileName()))
                            sources.with(uri.getPath(), 0);
                    }else{
                        int count = clipData.getItemCount();
                        AList<String> files = new AList<>(), paths = new AList<>();
                        String path;
                        for (int i = 0; i < count; i++){
                            uri = clipData.getItemAt(i).getUri();
                            path = uri.getPath();
                            index = sources.indexOf(path);
                            if (index > -1){
                                images.pop(index);
                                sources.pop(index);
                                imageViewerFragment.removeImage(index);
                            }else if (ImageUtils.saveImage(MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                                        Helper.ImageFolder,
                                                        filename = ImageUtils.newImageFileName(i))){
                                files.with(filename, 0);
                                paths.with(path, 0);
                            }
                        }
                        images.with(imageViewerFragment.addImages(files), 0);
                        sources.with(paths, 0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case ActivityCodeConstant.CROP_IMAGE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), crop_image_uri);
                    if (!ImageUtils.saveImage(image, Helper.ImageFolder, filename = ImageUtils.newImageFileName())) return;
                    int current = imageViewerFragment.getCurrent();
                    if (images.get(current).equals(food_cover)) food_cover = filename;
                    images.set(imageViewerFragment.setCurrentImage(filename), current);
                    sources.set("", current);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                return;
        }
        food_image.setVisibility(View.GONE);
        if (StringUtils.isEmpty(food_cover)) food_cover = images.first();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, ChooseTagFragment.TAG, chooseTagFragment);
        fragmentManager.putFragment(outState, ImageViewerFragment.TAG, imageViewerFragment);
        outState.putParcelable(FOOD, new Food(getFoodName(), images, chooseTagFragment.getData(), getNote(), like_toggle.isChecked(), food_cover));
    }
    public String getFoodName() { return edit_food_name.getText().toString().trim(); }
    private String getNote() { return edit_note.getText().toString().trim(); }

    public void finishWithFood(Food food){
        Intent intent = new Intent();
        intent.putExtra(FOOD, food);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish() {
        Helper.save();
        super.finish();
    }
}