package personalprojects.seakyluo.randommenu.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.HorizontalImageAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;

public class HorizontalImageViewFragment extends Fragment {
    public static final String TAG = "HorizontalImageAdderFragment";
    private HorizontalImageAdapter adapter;
    private Uri cameraImageUri;
    private ImageButton addImageButton;
    @Setter
    private int maxImages = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imageadder, container, false);
        RecyclerView envRecordRecyclerView = view.findViewById(R.id.env_recyclerview);
        addImageButton = view.findViewById(R.id.addImageButton);
        adapter = new HorizontalImageAdapter(getContext());

        addImageButton.setOnClickListener(this::showMenuFlyout);
        envRecordRecyclerView.setAdapter(adapter);
        return view;
    }

    public void setEditable(boolean editable){
        addImageButton.setVisibility(editable ? View.VISIBLE : View.GONE);
        adapter.setEditable(editable);
    }

    public void setData(List<String> images){
        adapter.setData(images);
    }

    public AList<String> getData(){
        return adapter.getData();
    }

    private void showMenuFlyout(View view){
        Activity activity = getActivity();
        final PopupMenuHelper helper = new PopupMenuHelper(R.menu.fetch_image_menu, activity, view);
        helper.removeItems(R.id.edit_image_item, R.id.remove_image_item, R.id.set_cover_item, R.id.move_to_first_item);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.open_camera_item:
                    cameraImageUri = ImageUtils.openCamera(activity, view);
                    return cameraImageUri != null;
                case R.id.open_gallery_item:
                    ImageUtils.openGallery(activity, view);
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
                showMenuFlyout(addImageButton);
                break;
            case ActivityCodeConstant.CAMERA:
                cameraImageUri = ImageUtils.openCamera(activity);
                break;
            case ActivityCodeConstant.READ_EXTERNAL_STORAGE_CODE:
                ImageUtils.openGallery(activity, addImageButton);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case ActivityCodeConstant.CAMERA:
                saveImage(cameraImageUri, cameraImageUri.getPath());
                break;
            case ActivityCodeConstant.GALLERY:
                ClipData clipData = data.getClipData();
                if (clipData == null) {
                    if (!addOrMoveImage(data.getData(), null)){
                        showMaxImagesExceededToast();
                    }
                } else {
                    for (int i = 0; i < clipData.getItemCount(); i++){
                        if (!addOrMoveImage(clipData.getItemAt(i).getUri(), i)){
                            showMaxImagesExceededToast();
                            break;
                        }
                    }
                }
                break;
        }
    }

    private boolean addOrMoveImage(Uri uri, Integer i){
        if (uri == null){
            return true;
        }
        int index = adapter.indexOf(uri.getPath());
        if (index > -1){
            adapter.move(index, 1);
        } else {
            if (maxImages > 0 && adapter.getItemCount() >= maxImages){
                return false;
            } else {
                saveImage(uri, ImageUtils.newImageFileName(i));
            }
        }
        return true;
    }

    private void saveImage(Uri uri, String filename){
        if (ImageUtils.saveImage(getContext(), uri, filename)){
            adapter.add(filename);
        }
    }

    private void showMaxImagesExceededToast(){
        Toast.makeText(getContext(), String.format("最多只能有%d张图片", maxImages), Toast.LENGTH_SHORT).show();
    }
}
