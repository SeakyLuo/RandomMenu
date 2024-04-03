package personalprojects.seakyluo.randommenu.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import personalprojects.seakyluo.randommenu.adapters.impl.ImageAdapter;
import personalprojects.seakyluo.randommenu.activities.FullScreenImageActivity;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.PermissionUtils;

import static android.app.Activity.RESULT_OK;

public class ImageViewerFragment extends Fragment {
    public static final String TAG = "ImageViewerFragment";
    private ImageAdapter adapter = new ImageAdapter(ImageView.ScaleType.CENTER_CROP);
    private ViewPager viewPager;
    private ImageButton prevImageButton, nextImageButton, cameraButton;
    private ImageView foodImage;
    private Uri cameraImageUri, cropImageUri;
    @Getter
    private String coverImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imageviewer, container, false);
        prevImageButton = view.findViewById(R.id.prev_image_button);
        nextImageButton = view.findViewById(R.id.next_image_button);
        foodImage = view.findViewById(R.id.food_image);
        viewPager = view.findViewById(R.id.imageViewPager);
        cameraButton = view.findViewById(R.id.camera_button);

        prevImageButton.setOnClickListener(v -> scrollTo(getCurrent() - 1));
        nextImageButton.setOnClickListener(v -> scrollTo(getCurrent() + 1));
        adapter.setDataChangedListener(images -> setButtonVisibility(getCurrent()));
        adapter.setContext(getContext());
        adapter.setImageClickedListener(v -> {
            Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.IMAGE, adapter.getData());
            intent.putExtra(FullScreenImageActivity.INDEX, getCurrent());
            startActivity(intent);
        });
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setButtonVisibility(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        cameraButton.setOnClickListener(v -> {
            if (PermissionUtils.checkAndRequestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, ActivityCodeConstant.WRITE_STORAGE)){
                showMenuFlyout();
            }
        });
        foodImage.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);

        scrollTo(getCurrent());
        setButtonVisibility(getCurrent());
        return view;
    }

    public List<String> getImages(){
        return adapter.getData();
    }
    
    private int getCurrent(){
        return viewPager.getCurrentItem();
    }

    public void setEditable(boolean editable){
        cameraButton.setVisibility(editable ? View.VISIBLE : View.GONE);
    }

    private void scrollTo(int index) {
        viewPager.setCurrentItem(index, true);
    }

    private void setButtonVisibility(int current){
        if (adapter.isEmpty()){
            prevImageButton.setVisibility(View.GONE);
            nextImageButton.setVisibility(View.GONE);
        } else {
            prevImageButton.setVisibility(current <= 0 ? View.GONE : View.VISIBLE);
            nextImageButton.setVisibility(current < 0 || current == adapter.getCount() - 1 ? View.GONE : View.VISIBLE);
        }
    }

    public void setImages(List<String> images, String cover) {
        coverImage = cover;
        adapter.setData(images);
        if (viewPager != null){
            foodImage.setVisibility(images == null || images.isEmpty() ? View.VISIBLE : View.GONE);
            scrollTo(0);
            setButtonVisibility(0);
        }
    }

    public String getCurrentImage() {
        return adapter.get(getCurrent());
    }

    private void removeCurrentImage() {
        int current = getCurrent();
        String image = adapter.get(current);
        adapter.remove(current);
    }

    private String addImage(String data) {
        adapter.add(data);
        scrollTo(0);
        return data;
    }

    private void move(int from, int to) {
        adapter.move(from, to);
        if (getCurrent() == from){
            scrollTo(to);
        }
    }

    private void updateImage(String image, int index){
        if (adapter.get(index).equals(coverImage)){
            setCover(image);
        }
        adapter.set(image, index);
    }

    private void setCover(String image){
        coverImage = image;
    }

    private void showMenuFlyout(){
        Activity activity = getActivity();
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, activity, cameraButton);
        int count = adapter.getCount();
        if (count == 0) helper.removeItems(R.id.edit_image_item, R.id.remove_image_item);
        if (count < 2 || getCurrentImage().equals(coverImage)) helper.removeItems(R.id.set_cover_item);
        if (count <= 1 || getCurrent() <= 0) helper.removeItems(R.id.move_to_first_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    cameraImageUri = ImageUtils.openCamera(activity);
                    return cameraImageUri != null;
                case R.id.open_gallery_item:
                    ImageUtils.openGallery(activity);
                    return true;
                case R.id.edit_image_item:
                    cropImageUri = ImageUtils.cropImage(activity, ImageUtils.getImagePath(getCurrentImage()));
                    return cropImageUri != null;
                case R.id.remove_image_item:
                    String image = getCurrentImage();
                    removeCurrentImage();
                    if (image.equals(coverImage)) setCover(adapter.isEmpty() ? null : adapter.get(0));
                    if (adapter.isEmpty()) foodImage.setVisibility(View.VISIBLE);
                    return true;
                case R.id.set_cover_item:
                    setCover(getCurrentImage());
                    return true;
                case R.id.move_to_first_item:
                    move(getCurrent(), 0);
                    return true;
            }
            return false;
        });
        helper.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            return;
        Activity activity = getActivity();
        switch (requestCode){
            case ActivityCodeConstant.WRITE_STORAGE:
                showMenuFlyout();
                break;
            case ActivityCodeConstant.CAMERA:
                cameraImageUri = ImageUtils.openCamera(activity);
                break;
            case ActivityCodeConstant.READ_EXTERNAL_STORAGE_CODE:
                ImageUtils.openGallery(activity);
                break;
        }
    }

    private boolean saveImage(Uri uri, String filename, boolean update){
        if (!ImageUtils.saveImage(getContext(), uri, filename)){
            return false;
        }
        if (update){
            updateImage(filename, getCurrent());
        } else {
            addImage(filename);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case ActivityCodeConstant.CAMERA:
                saveImage(cameraImageUri, cameraImageUri.getPath(), false);
                break;
            case ActivityCodeConstant.GALLERY:
                ClipData clipData = data.getClipData();
                if (clipData == null) {
                    addOrMoveImage(data.getData(), null);
                } else {
                    for (int i = 0; i < clipData.getItemCount(); i++){
                        addOrMoveImage(clipData.getItemAt(i).getUri(), i);
                    }
                }
                break;
            case ActivityCodeConstant.CROP_IMAGE:
                saveImage(cropImageUri, ImageUtils.newImageFileName(), true);
                break;
            default:
                return;
        }
        foodImage.setVisibility(View.GONE);
        if (StringUtils.isEmpty(coverImage) && !adapter.isEmpty()){
            setCover(adapter.get(0));
        }
    }

    private boolean addOrMoveImage(Uri uri, Integer i){
        if (uri == null){
            return false;
        }
        int index = adapter.indexOf(uri.getPath());
        if (index > -1){
            move(index, 0);
            return true;
        } else {
            return saveImage(uri, ImageUtils.newImageFileName(i), false);
        }
    }
}
