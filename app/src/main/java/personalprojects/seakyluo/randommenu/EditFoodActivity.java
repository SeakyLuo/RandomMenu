package personalprojects.seakyluo.randommenu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class EditFoodActivity extends AppCompatActivity {
    public static final int CAMERA_CODE = 0, GALLERY_CODE = 1, WRITE_STORAGE = 3, FOOD_CODE = 4, CROP_CODE = 5;
    public static final String FOOD = "Food", DELETE = "Delete";
    private ImageButton camera_button;
    private EditText edit_food_name, edit_note;
    private ImageView food_image;
    private Switch like_toggle;
    private ChooseTagFragment chooseTagFragment;
    private ImageViewerFragment imageViewerFragment;
    private Food currentFood;
    private Uri camera_image_uri, crop_image_uri;
    private AList<String> images = new AList<>();
    private boolean isDraft = false;

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
        }else{
            chooseTagFragment = (ChooseTagFragment) fragmentManager.getFragment(savedInstanceState, ChooseTagFragment.TAG);
            imageViewerFragment = (ImageViewerFragment) fragmentManager.getFragment(savedInstanceState, ImageViewerFragment.TAG);
            currentFood = savedInstanceState.getParcelable(FOOD);
        }
        isDraft = Settings.settings.FoodDraft != null && Settings.settings.FoodDraft.equals(currentFood);
        setFood(currentFood);

        cancel_button.setOnClickListener(v -> {
            String food_name = edit_food_name.getText().toString(), note = edit_note.getText().toString();
            AList<Tag> tags = chooseTagFragment.GetData();
            boolean nameChanged = currentFood == null ? food_name.length() > 0 : !food_name.equals(currentFood.Name),
                    imageChanged = currentFood == null ? images.Count() > 0 : !images.SameCollection(currentFood.Images),
                    tagChanged = currentFood == null ? tags.Count() > 0 : !tags.SameCollection(currentFood.GetTags()),
                    noteChanged = currentFood == null ? note.length() > 0 : !note.equals(currentFood.Note),
                    likeChanged = currentFood != null && like_toggle.isChecked() != currentFood.IsFavorite();
            if (nameChanged || imageChanged || tagChanged || noteChanged || likeChanged){
                AskYesNoDialog dialog = new AskYesNoDialog();
                dialog.showNow(fragmentManager, AskYesNoDialog.TAG);
                dialog.setMessage(getString(R.string.save_as_draft));
                dialog.setOnYesListener(view -> {
                    Settings.settings.FoodDraft = new Food(food_name, images, tags, note, like_toggle.isChecked());
                    finish();
                });
                dialog.setOnNoListener(view -> finish());
            }else{
                finish();
            }
        });
        confirm_button.setOnClickListener(v -> {
            String food_name = getFoodName();
            if (food_name.length() == 0){
                Toast.makeText(this, getString(R.string.empty_food_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (Settings.settings.Foods.Any(f -> f.Name.equals(food_name)) && (currentFood == null || !currentFood.Name.equals(food_name))){
                Toast.makeText(this, getString(R.string.food_exists), Toast.LENGTH_SHORT).show();
                return;
            }
            AList<Tag> tags = chooseTagFragment.GetData();
            if (tags.IsEmpty()){
                Toast.makeText(this, getString(R.string.at_least_one_tag), Toast.LENGTH_SHORT).show();
                return;
            }
            String note = getNote();
            Food food = new Food(food_name, images, tags, note, like_toggle.isChecked());
            if (currentFood == null) Settings.settings.AddFood(food);
            else if (isDraft){
                Settings.settings.AddFood(food);
                Settings.settings.FoodDraft = null;
            }else{
                Settings.settings.UpdateFood(currentFood, food);
            }
            Intent intent = new Intent();
            intent.putExtra(FOOD, food);
            setResult(RESULT_OK, intent);
            finish();
        });
        camera_button.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_STORAGE);
            else
                ShowMenuFlyout();
        });
        delete_food_button.setVisibility(getIntent().getBooleanExtra(DELETE, false) ? View.VISIBLE : View.GONE);
        delete_food_button.setOnClickListener(v -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.showNow(fragmentManager, AskYesNoDialog.TAG);
            dialog.setMessage(getString(R.string.ask_delete_food));
            dialog.setOnYesListener(view -> {
                if (isDraft) Settings.settings.FoodDraft = null;
                else Settings.settings.RemoveFood(currentFood);
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private void ShowMenuFlyout(){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, this, camera_button);
        if (images.IsEmpty()) helper.removeItems(R.id.edit_image_item, R.id.remove_image_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                        requestPermissions(new String[]{ Manifest.permission.CAMERA }, CAMERA_CODE);
                    else
                        OpenCamera();
                    return true;
                case R.id.open_gallery_item:
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, GALLERY_CODE);
                    else
                        OpenGallery();
                    return true;
                case R.id.edit_image_item:
                    return CropImage();
                case R.id.remove_image_item:
                    images.Pop(imageViewerFragment.removeCurrentImage());
                    if (images.IsEmpty()) food_image.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private boolean CropImage(){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.parse(CurrentImage()), "image/*");
            crop_image_uri = Uri.fromFile(File.createTempFile("tempCrop", ".jpg", Helper.TempFolder));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, crop_image_uri);
            startActivityForResult(intent, CROP_CODE);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Whoops - your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void OpenCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_image_uri = Helper.GetFileUri(this, Helper.NewImageFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_image_uri);
        startActivityForResult(intent, CAMERA_CODE);
    }

    private void OpenGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), GALLERY_CODE);
    }

    private void setFood(Food food){
        if (food == null) return;
        edit_food_name.setText(food.Name);
        food_image.setVisibility(food.HasImage() ? View.GONE : View.VISIBLE);
        imageViewerFragment.setImages(images.CopyFrom(food.Images));
        chooseTagFragment.SetData(food.GetTags());
        edit_note.setText(food.Note);
        like_toggle.setChecked(food.IsFavorite());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            return;
        switch (requestCode){
            case WRITE_STORAGE:
                ShowMenuFlyout();
                break;
            case CAMERA_CODE:
                OpenCamera();
                break;
            case GALLERY_CODE:
                OpenGallery();
                break;
        }
    }

    private String CurrentImage() { return images.Get(imageViewerFragment.getCurrent()); }
    private void AddImage(String image) { images.Add(imageViewerFragment.adapter.Add(image), 0) ; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bitmap image;
        switch (requestCode){
            case CAMERA_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), camera_image_uri);
                    AddImage(Helper.SaveImage(image, Helper.ImageFolder, camera_image_uri.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case GALLERY_CODE:
                try {
                    if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        int count = clipData.getItemCount();
                        AList<String> paths = new AList<>();
                        for (int i = 0; i < count; i++)
                            paths.Add(Helper.SaveImage(MediaStore.Images.Media.getBitmap(getContentResolver(), clipData.getItemAt(i).getUri()),
                                                       Helper.ImageFolder,
                                                       Helper.NewImageFileName(i)));
                        images.AddAll(imageViewerFragment.adapter.Add(paths), 0);
                    }else{
                        AddImage(Helper.SaveImage(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()),
                                                  Helper.ImageFolder,
                                                  Helper.NewImageFileName()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case CROP_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), crop_image_uri);
                    images.Set(imageViewerFragment.setCurrentImage(Helper.SaveImage(image, Helper.ImageFolder, Helper.NewImageFileName())), imageViewerFragment.getCurrent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                return;
        }
        food_image.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.putFragment(outState, ChooseTagFragment.TAG, chooseTagFragment);
        fragmentManager.putFragment(outState, ImageViewerFragment.TAG, imageViewerFragment);
        outState.putParcelable(FOOD, new Food(getFoodName(), images, chooseTagFragment.GetData(), getNote(), like_toggle.isChecked()));
    }
    private String getFoodName() { return edit_food_name.getText().toString().trim(); }
    private String getNote() { return edit_note.getText().toString().trim(); }

    @Override
    public void finish() {
        Helper.Save();
        super.finish();
    }
}