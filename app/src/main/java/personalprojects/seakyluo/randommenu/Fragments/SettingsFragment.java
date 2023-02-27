package personalprojects.seakyluo.randommenu.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.dialogs.LoadingDialog;
import personalprojects.seakyluo.randommenu.activities.impl.DislikeActivity;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.activities.impl.MainActivity;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.activities.impl.MoreSettingsActivity;
import personalprojects.seakyluo.randommenu.activities.impl.MyFavoritesActivity;
import personalprojects.seakyluo.randommenu.activities.impl.NoteActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.ToCookActivity;
import personalprojects.seakyluo.randommenu.activities.impl.ToEatActivity;
import personalprojects.seakyluo.randommenu.utils.FileUtils;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
    private static final int FILE_PICKER = 1;
    public static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.findViewById(R.id.to_cook_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ToCookActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.my_favorites_button).setOnClickListener(v -> {
            startActivityForResult(new Intent(getContext(), MyFavoritesActivity.class), MyFavoritesActivity.REQUEST_CODE);
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.to_eat_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ToEatActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.dislike_food_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), DislikeActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.dislike_food_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), DislikeActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.note_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NoteActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.more_settings_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MoreSettingsActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, 0);
        });
        view.findViewById(R.id.clear_cache_button).setOnClickListener(v -> {
            LoadingDialog dialog = new LoadingDialog();
            dialog.setOnViewCreatedListener(d -> {
                dialog.setMessage(R.string.clearing_cache);
                new Thread(() -> {
                    // Removing unused images
                    if (Settings.settings.Foods.size() > 0){
                        Set<String> paths = Settings.settings.Foods.stream().flatMap(f -> f.Images.stream()).collect(Collectors.toSet());
                        for (File file: Helper.ImageFolder.listFiles())
                            if (!paths.contains(file.getName()))
                                file.delete();
                    }
                    // Removing non-existent images
                    Set<String> files = new AList<>(Helper.ImageFolder.listFiles()).convert(File::getName).toSet();
                    Settings.settings.Foods.ForEach(food -> {
                       food.Images.copy().ForEach(image -> {
                           if (!files.contains(image)) food.Images.remove(image);
                       });
                    });
                    clearFolder(Helper.LogFolder);
                    clearFolder(Helper.TempFolder);
                    clearFolder(Helper.TempUnzipFolder);
                    File[] exportedFiles = Helper.ExportedDataFolder.listFiles();
                    if (exportedFiles != null && exportedFiles.length > 1){
                        new AList<>(exportedFiles).after(0).ForEach(File::delete);
                    }
                    showShortToast(dialog, R.string.clear_cache_msg);
                }).start();
            });
            dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        });
        view.findViewById(R.id.import_data_button).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICKER);
            } catch (ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.export_data_button).setOnClickListener(v -> {
            String filename = "RandomMenu" + Helper.formatCurrentTimestamp() + ".zip", path = Helper.ExportedDataFolder.getPath() + File.separator + filename;
            LoadingDialog dialog = new LoadingDialog();
            dialog.setOnViewCreatedListener(d -> {
                dialog.setMessage(R.string.exporting_data);
                new Thread(() -> {
                    try{
                        FileUtils.zip(path, Helper.ImageFolder, new File(FileUtils.getPath(Settings.FILENAME)));
                    }catch (FileNotFoundException e){
                        showExceptionToast(dialog, R.string.file_not_found, e);
                        return;
                    } catch (Exception e) {
                        showExceptionToast(dialog, R.string.export_data_failed, e);
                        return;
                    }
                    showShortToast(dialog, R.string.export_data_msg);
                    // 分享导出文件
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FileUtils.getFileUri(getContext(), path));
                    startActivity(Intent.createChooser(shareIntent, String.format(getString(R.string.share_item), filename)));
                }).start();
            });
            dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        });
        view.findViewById(R.id.save_data_button).setOnClickListener(v -> {
            Settings.settings.Tags.ForEach(t -> {
                t.setCounter(Settings.settings.Foods.find(f -> f.hasTag(t)).size());
            });
            Settings.settings.sortTags();
            Helper.save();
            Toast.makeText(getContext(), R.string.data_saved, Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MyFavoritesActivity.REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    ((MainActivity)getActivity()).randomFragment.Refresh();
                }
                break;
            case FILE_PICKER:
                if (data == null || data.getData() == null){
                    return;
                }
                File file = new File(data.getData().getPath());
                unzip(file);
                break;
        }
    }

    public void unzip(File file){
        if (!file.getName().endsWith(".zip")){
            Toast.makeText(getContext(), R.string.illegal_unzip_target, Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog dialog = new LoadingDialog();
        dialog.setOnViewCreatedListener(d -> {
            new Thread(() -> {
                clearFolder(Helper.TempUnzipFolder);
                try {
                    FileUtils.unzip(file, Helper.TempUnzipFolder);
                } catch (Exception e) {
                    showExceptionToast(dialog, R.string.import_data_failed, e);
                    return;
                }
                AList<File> files = new AList<>(Helper.TempUnzipFolder.listFiles());
                File imageFolder = files.first(f -> f.getName().equals(Helper.ImageFolder.getName()));
                if (imageFolder == null){
                    showShortToast(dialog, R.string.no_import_image_folder);
                }else{
                    try {
                        FileUtils.copy(imageFolder, Helper.root);
                    } catch (IOException e) {
                        showExceptionToast(dialog, R.string.import_data_failed, e);
                        return;
                    }
                }
                File settings = files.first(f -> f.getName().equals(Settings.FILENAME));
                if (settings == null){
                    showShortToast(dialog, R.string.no_import_settings);
                    return;
                }else{
                    try {
                        FileUtils.copy(settings, Helper.root);
                    } catch (IOException e) {
                        showExceptionToast(dialog, R.string.import_data_failed, e);
                        return;
                    }
                }
                Helper.init(getActivity());
                Helper.save();
                clearFolder(Helper.TempUnzipFolder);
            }).start();
        });
        dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        dialog.setMessage(R.string.importing_data);
    }

    private static void clearFolder(File folder){
        if (folder == null){
            return;
        }
        for (File file: folder.listFiles()){
            file.delete();
        }
    }

    private void showExceptionToast(LoadingDialog dialog, int message, Exception e){
        showToast(dialog, getString(message) + (e.getMessage() == null ? e.toString() : e.getMessage()), Toast.LENGTH_LONG);
    }

    private void showShortToast(LoadingDialog dialog, String message){
        showToast(dialog, message, Toast.LENGTH_SHORT);
    }

    private void showLongToast(LoadingDialog dialog, int message){
        showToast(dialog, message, Toast.LENGTH_LONG);
    }

    private void showShortToast(LoadingDialog dialog, int message){
        showToast(dialog, message, Toast.LENGTH_SHORT);
    }

    private void showToast(LoadingDialog dialog, String message, int length){
        getActivity().runOnUiThread(() -> {
            dialog.dismiss();
            Toast.makeText(getContext(), message, length).show();
        });
    }

    private void showToast(LoadingDialog dialog, int message, int length){
        getActivity().runOnUiThread(() -> {
            dialog.dismiss();
            Toast.makeText(getContext(), message, length).show();
        });
    }
}
