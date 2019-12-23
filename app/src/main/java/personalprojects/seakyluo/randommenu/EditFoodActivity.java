package personalprojects.seakyluo.randommenu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class EditFoodActivity extends AppCompatActivity {
    public static final int CAMERA_CODE = 0, GALLERY_CODE = 1, WRITE_STORAGE = 3, FOOD_CODE = 4, CROP_CODE = 5;
    public static final String FOOD = "Food";
    private ImageButton camera_button;
    private EditText edit_food_name, edit_note;
    private ImageView food_image;
    private Switch like_toggle;
    private ChooseTagFragment fragment;
    private boolean SetFoodImage = false;
    private Food intent_food;
    private Uri camera_image_uri, crop_image_uri;

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
        if (savedInstanceState == null){
            fragment = (ChooseTagFragment) getSupportFragmentManager().findFragmentById(R.id.choose_tag_fragment);
            setFood(intent_food = getIntent().getParcelableExtra(FOOD));
        }else{
            fragment = (ChooseTagFragment) getSupportFragmentManager().getFragment(savedInstanceState, ChooseTagFragment.TAG);
            setFood(savedInstanceState.getParcelable(FOOD));
        }

        cancel_button.setOnClickListener(v -> {
            String food_name = edit_food_name.getText().toString(), note = edit_note.getText().toString();
            AList<Tag> tags = fragment.GetData();
            boolean nameChanged = intent_food == null ? food_name.length() > 0 : !food_name.equals(intent_food.Name),
                    tagChanged = intent_food == null ? tags.Count() > 0 : !tags.SameCollection(intent_food.GetTags()),
                    noteChanged = intent_food == null ? note.length() > 0 : !note.equals(intent_food.Note);
            if (nameChanged || tagChanged || noteChanged){
                AskYesNoDialog dialog = new AskYesNoDialog();
                dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
                dialog.setMessage(getString(R.string.save_as_draft));
                dialog.setOnYesListener(view -> {
                    String image_path = SetFoodImage ? Helper.SaveImage(food_image, Helper.TempFolder, Helper.NewImageFileName()) : "";
                    Settings.settings.FoodDraft = new Food(food_name, image_path, tags, note, like_toggle.isChecked());
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
            if (Settings.settings.Foods.Any(f -> f.Name.equals(food_name)) && (intent_food == null || !intent_food.Name.equals(food_name))){
                Toast.makeText(this, getString(R.string.food_exists), Toast.LENGTH_SHORT).show();
                return;
            }
            AList<Tag> tags = fragment.GetData();
            if (tags.Count() == 0){
                Toast.makeText(this, getString(R.string.at_least_one_tag), Toast.LENGTH_SHORT).show();
                return;
            }
            String note = edit_note.getText().toString().trim();
            Food food = new Food(food_name, SetFoodImage ? Helper.SaveImage(food_image, Helper.ImageFolder, Helper.NewImageFileName()) : intent_food == null ? "" : intent_food.ImagePath, tags, note, like_toggle.isChecked());
            if (intent_food == null) Settings.settings.AddFood(food);
            else if (intent_food.equals(Settings.settings.FoodDraft)){
                Settings.settings.AddFood(food);
                Settings.settings.FoodDraft = null;
            }else{
                if (intent_food.HasImage() && !intent_food.ImagePath.equals(food.ImagePath))
                    new File(intent_food.ImagePath).delete();
                Settings.settings.UpdateFood(intent_food, food);
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
        food_image.setOnClickListener(v -> {
            if (intent_food == null ? !SetFoodImage : !intent_food.HasImage()) return;
            Intent intent = new Intent(this, FullScreenImageActivity.class);
            if (SetFoodImage) FullScreenImageActivity.image = Helper.GetFoodBitmap(food_image);
            else intent.putExtra(FullScreenImageActivity.IMAGE, intent_food.ImagePath);
            startActivity(intent);
        });
        delete_food_button.setVisibility(intent_food == null ? View.GONE : View.VISIBLE);
        delete_food_button.setOnClickListener(v -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
            dialog.setMessage(getString(R.string.ask_delete_food));
            dialog.setOnYesListener(view -> {
                if (intent_food.equals(Settings.settings.FoodDraft)) Settings.settings.FoodDraft = null;
                else Settings.settings.RemoveFood(intent_food);
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private void ShowMenuFlyout(){
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, this, camera_button);
        if (intent_food == null ? !SetFoodImage : Helper.IsNullOrEmpty(intent_food.ImagePath)) helper.removeItem(R.id.edit_image_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.edit_image_item:
                    return CropImage();
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
            }
            return false;
        });
        helper.show();
    }

    private boolean CropImage(){
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (camera_image_uri == null){
                intent.setDataAndType(Uri.parse(intent_food.ImagePath), "image/*");
                crop_image_uri = Uri.fromFile(File.createTempFile("tempCrop", ".jpg", Helper.TempFolder));
            }else{
                intent.setDataAndType(camera_image_uri, "image/*");
                crop_image_uri = camera_image_uri;
            }
            // indicate image type and Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, crop_image_uri);
            startActivityForResult(intent, CROP_CODE);
            return true;
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Whoops - your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void OpenCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            camera_image_uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",
                                                                File.createTempFile("tempCamera", ".jpg", Helper.TempFolder));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_image_uri);
            startActivityForResult(intent, CAMERA_CODE);
        } catch (IOException e) {
            camera_image_uri = null;
        }
    }

    private void OpenGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_CODE);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
    }

    private void setFood(Food food){
        if (food == null) return;
        edit_food_name.setText(food.Name);
        if (food.HasImage()) Helper.LoadImage(Glide.with(this), food.ImagePath, food_image);
        fragment.SetData(food.GetTags());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        Bitmap image = null;
        switch (requestCode){
            case CAMERA_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), camera_image_uri);
                    food_image.setImageBitmap(image);
                    SetFoodImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case GALLERY_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), camera_image_uri = data.getData());
                    food_image.setImageBitmap(image);
                    SetFoodImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CROP_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), crop_image_uri);
                    food_image.setImageBitmap(image);
                    SetFoodImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, ChooseTagFragment.TAG, fragment);
        outState.putParcelable(FOOD, new Food(getFoodName()));
    }
    private String getFoodName() { return edit_food_name.getText().toString().trim(); }
    private String getNote() { return edit_note.getText().toString().trim(); }

    @Override
    public void finish() {
        SetFoodImage = false;
        camera_image_uri = crop_image_uri = null;
        Helper.Save(this);
        super.finish();
    }
}