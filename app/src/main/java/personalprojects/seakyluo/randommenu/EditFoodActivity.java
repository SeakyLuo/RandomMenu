package personalprojects.seakyluo.randommenu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditFoodActivity extends AppCompatActivity {
    public static final int CAMERA_CODE = 0, GALLERY_CODE = 1, CHOOSE_TAG_CODE = 2;
    public static final String FOOD = "Food";
    private Food intent_food;
    private ImageButton cancel_button, confirm_button, camera_button, add_tag_button;
    private EditText edit_food_name, edit_note;
    private ImageView food_image;
    private String image_path;
    private TagFragment tagFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        cancel_button = findViewById(R.id.cancel_button);
        confirm_button = findViewById(R.id.confirm_button);
        camera_button = findViewById(R.id.camera_button);
        add_tag_button = findViewById(R.id.add_tag_button);
        edit_food_name = findViewById(R.id.edit_food_name);
        food_image = findViewById(R.id.food_image);

        intent_food = getIntent().getParcelableExtra(FOOD);
        if (intent_food != null){
            edit_food_name.setText(intent_food.Name);
            if (intent_food.HasImage()) food_image.setImageBitmap(Helper.GetFoodBitmap(intent_food));
            tagFragment.SetData(intent_food.GetTags(), true);
            edit_note.setText(intent_food.Note);
        }

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String food_name = edit_food_name.getText().toString();
                if (food_name.length() == 0){
                    Toast.makeText(getApplicationContext(), "Name Too Short!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Settings.settings.ContainsFood(food_name)){
                    Toast.makeText(getApplicationContext(), "Food Exists!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Tag> tags = (ArrayList<Tag>) tagFragment.GetTags();
                if (tags.size() == 0){
                    Toast.makeText(getApplicationContext(), "At least 1 tag!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String note = edit_note.getText().toString();
                Food food = new Food(food_name, image_path, tags, note);
                Settings.settings.AddFood(food);
                finish();
            }
        });
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    requestPermissions(new String[]{ Manifest.permission.CAMERA }, CAMERA_CODE);
                }else{
                    OpenCamera();
                }
            }
        });
        add_tag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseTagActivity.class);
                startActivityForResult(intent, CHOOSE_TAG_CODE);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.tags_frame, tagFragment = new TagFragment()).commit();
    }

    private void OpenCamera(){
        File file = new File(Helper.ImageFolder.getPath() + File.separator + NewImageFileName());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        image_path = uri.getPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_CODE);
    }

    private void OpenGallery(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_CODE);
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_CODE);
    }

    private String NewImageFileName(){
        // Create an image file name
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            return;
        switch (requestCode){
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
                image = Helper.GetFoodBitmap(image_path);
                food_image.setImageBitmap(image);
                break;
            case GALLERY_CODE:
                try {
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    food_image.setImageBitmap(image);
                    // Then saves to local
                    try (FileOutputStream out = new FileOutputStream(image_path = Helper.GetImagePath(NewImageFileName()))) {
                        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (IOException e) {
                        image_path = "";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CHOOSE_TAG_CODE:
                Tag tag = new Tag(data.getStringExtra(ChooseTagActivity.TAG));
                ToggleTag toggleTag = new ToggleTag(tag, true);
                if (tagFragment.GetData().contains(toggleTag)){
                    Toast.makeText(this, "Tags Already Exists", Toast.LENGTH_SHORT).show();
                }else{
                    tagFragment.Add(toggleTag);
                    add_tag_button.setVisibility(tagFragment.CountTags() == 10 ? View.GONE : View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }
}
