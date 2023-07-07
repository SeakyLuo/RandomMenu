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

import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.LoadingDialog;
import personalprojects.seakyluo.randommenu.activities.impl.DislikeActivity;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.activities.MainActivity;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.activities.impl.MoreSettingsActivity;
import personalprojects.seakyluo.randommenu.activities.impl.MyFavoritesActivity;
import personalprojects.seakyluo.randommenu.activities.impl.NoteActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.ToCookActivity;
import personalprojects.seakyluo.randommenu.activities.impl.ToEatActivity;
import personalprojects.seakyluo.randommenu.models.TagMapEntry;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.services.ImagePathService;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
    private static final int FILE_PICKER = 1;
    public static final String TAG = "SettingsFragment";
    private static final String RESTAURANT_FILENAME = "restaurants.json", SELF_FOOD_FILENAME = "selfFoods.json",
            AUTO_TAG_MAP_FILENAME = "autoTagMap.json";

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
        view.findViewById(R.id.clear_cache_button).setOnClickListener(v -> clearCache());
        view.findViewById(R.id.import_data_button).setOnClickListener(v -> importData());
        view.findViewById(R.id.export_data_button).setOnClickListener(v -> showExportDataDialog());
        view.findViewById(R.id.fix_data_button).setOnClickListener(v -> {

            Toast.makeText(getContext(), "修复成功", Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    private void clearLocalImages(Collection<String> using){
        if (CollectionUtils.isEmpty(using)){
            return;
        }
        Set<String> paths = new HashSet<>(using);
        for (File file : FileUtils.IMAGE_FOLDER.listFiles()) {
            if (!paths.contains(file.getName())){
                file.delete();
            }
        }
    }

    private void clearCache(){
        LoadingDialog dialog = new LoadingDialog();
        dialog.setOnViewCreatedListener(d -> {
            dialog.setMessage(R.string.clearing_cache);
            new Thread(() -> {
                // Removing unused images
                clearLocalImages(ImagePathService.selectPaths());
                // Removing non-existent images
                List<String> filenames = Arrays.stream(FileUtils.IMAGE_FOLDER.listFiles()).map(File::getName).collect(Collectors.toList());
                SelfMadeFoodService.deleteNonExistentImage(filenames);
                clearFolder(FileUtils.LOG_FOLDER);
                clearFolder(FileUtils.TEMP_FOLDER);
                clearFolder(FileUtils.TEMP_UNZIP_FOLDER);
                File[] exportedFiles = FileUtils.EXPORTED_DATA_FOLDER.listFiles();
                if (exportedFiles != null && exportedFiles.length > 1){
                    Arrays.stream(exportedFiles).skip(1).forEach(File::delete);
                }
                showShortToast(dialog, R.string.clear_cache_msg);
            }).start();
        });
        dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
    }

    private void importData(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择需要导入的文件"), FILE_PICKER);
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExportDataDialog(){
        String filename = "RandomMenu" + Helper.formatCurrentTimestamp() + ".zip", path = FileUtils.EXPORTED_DATA_FOLDER.getPath() + File.separator + filename;
        LoadingDialog dialog = new LoadingDialog();
        dialog.setOnViewCreatedListener(d -> {
            dialog.setMessage(R.string.exporting_data);
            new Thread(() -> exportData(dialog, filename, path)).start();
        });
        dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
    }

    private static String buildRestaurantFilename(){
        return FileUtils.TEMP_FOLDER.getName() + File.separator + RESTAURANT_FILENAME;
    }

    private static String buildSelfFoodFilename(){
        return FileUtils.TEMP_FOLDER.getName() + File.separator + SELF_FOOD_FILENAME;
    }

    private static String buildAutoTagMapFilename(){
        return FileUtils.TEMP_FOLDER.getName() + File.separator + AUTO_TAG_MAP_FILENAME;
    }

    private File exportToFile(String filename, Object data){
        FileUtils.writeFile(filename, JsonUtils.toJson(data));
        return FileUtils.getFile(filename);
    }

    private void exportData(LoadingDialog dialog, String filename, String path){
        try {
            File settings = FileUtils.getFile(Settings.FILENAME);

            File restaurantsFile = exportToFile(buildRestaurantFilename(), RestaurantDaoService.selectAll());
            File selfFoodsFile = exportToFile(buildSelfFoodFilename(), SelfMadeFoodService.selectAll());
            File autoTagMapFile = exportToFile(buildAutoTagMapFilename(), AutoTagMapperDaoService.selectAll());

            dialog.setMessage("正在打包，请稍候");
            FileUtils.zip(path, FileUtils.IMAGE_FOLDER, settings, restaurantsFile, selfFoodsFile, autoTagMapFile);
        } catch (FileNotFoundException e){
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
    }

    private void showUnzipDialog(File file){
        if (!file.getName().endsWith(".zip")){
            Toast.makeText(getContext(), R.string.illegal_unzip_target, Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog dialog = new LoadingDialog();
        dialog.setOnViewCreatedListener(d -> new Thread(() -> unzip(dialog, file)).start());
        dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        dialog.setMessage(R.string.importing_data);
    }

    private void unzip(LoadingDialog dialog, File file){
        clearFolder(FileUtils.TEMP_UNZIP_FOLDER);
        try {
            dialog.setMessage("正在解压");
            FileUtils.unzip(file, FileUtils.TEMP_UNZIP_FOLDER);
        } catch (Exception e) {
            showExceptionToast(dialog, R.string.import_data_failed, e);
            return;
        }
        AList<File> files = new AList<>(FileUtils.TEMP_UNZIP_FOLDER.listFiles());
        File settings = files.first(f -> f.getName().equals(Settings.FILENAME));
        if (settings == null){
            showShortToast(dialog, R.string.no_import_settings);
            return;
        }
        try {
            FileUtils.copy(settings, FileUtils.ROOT_FOLDER);
        } catch (IOException e) {
            showExceptionToast(dialog, R.string.import_data_failed, e);
            return;
        }
        File imageFolder = files.first(f -> f.getName().equals(FileUtils.IMAGE_FOLDER.getName()));
        if (imageFolder == null){
            showShortToast(dialog, R.string.no_import_image_folder);
        } else {
            try {
                dialog.setMessage("正在导入图片");
                FileUtils.copy(imageFolder, FileUtils.ROOT_FOLDER);
            } catch (IOException e) {
                showExceptionToast(dialog, R.string.import_data_failed, e);
            }
        }
        try {
            dialog.setMessage("正在导入探店记录");
            String json = FileUtils.readFile(buildRestaurantFilename());
            List<RestaurantVO> list = JsonUtils.fromJson(json, new TypeToken<List<RestaurantVO>>() {});
            RestaurantDaoService.selectAll().forEach(RestaurantDaoService::delete);
            RestaurantDaoService.insert(list);
        } catch (Exception e){
            showExceptionToast(dialog, "导入探店记录失败", e);
        }
        try {
            dialog.setMessage("正在导入菜肴");
            String json = FileUtils.readFile(buildSelfFoodFilename());
            List<SelfMadeFood> list = JsonUtils.fromJson(json, new TypeToken<List<SelfMadeFood>>() {});
            SelfFoodDaoService.selectAll().forEach(SelfFoodDaoService::delete);
            list.forEach(SelfMadeFoodService::addFood);
        } catch (Exception e){
            showExceptionToast(dialog, "导入菜肴记录失败", e);
        }
        try {
            dialog.setMessage("正在导入标签映射");
            String json = FileUtils.readFile(buildAutoTagMapFilename());
            List<TagMapEntry> list = JsonUtils.fromJson(json, new TypeToken<List<TagMapEntry>>() {});
            AutoTagMapperDaoService.selectAll().forEach(AutoTagMapperDaoService::delete);
            list.forEach(AutoTagMapperDaoService::insert);
        } catch (Exception e){
            showExceptionToast(dialog, "导入标签映射记录失败", e);
        }
        Helper.init(getActivity());
        Helper.save();
        clearFolder(FileUtils.TEMP_UNZIP_FOLDER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case MyFavoritesActivity.REQUEST_CODE:
                ((MainActivity)getActivity()).randomFragment.refresh();
                break;
            case FILE_PICKER:
                if (data == null || data.getData() == null){
                    return;
                }
                File file = new File(data.getData().getPath());
                showUnzipDialog(file);
                break;
        }
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
        showToast(dialog, getString(message) + "：" + (e.getMessage() == null ? e.toString() : e.getMessage()), Toast.LENGTH_LONG);
    }

    private void showExceptionToast(LoadingDialog dialog, String message, Exception e){
        showToast(dialog, message + "：" + (e.getMessage() == null ? e.toString() : e.getMessage()), Toast.LENGTH_LONG);
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
