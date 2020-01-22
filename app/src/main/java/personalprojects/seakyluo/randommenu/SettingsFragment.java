package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
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
        view.findViewById(R.id.delete_draft_button).setOnClickListener(v -> {
            Settings.settings.FoodDraft = null;
            Toast.makeText(getContext(), R.string.draft_deleted, Toast.LENGTH_SHORT).show();
        });
        view.findViewById(R.id.clear_cache_button).setOnClickListener(v -> {
            LoadingDialog dialog = new LoadingDialog();
            dialog.setOnViewCreatedListener(d -> {
                dialog.setMessage(R.string.clearing_cache);
                new Thread(() -> {
                    HashSet<String> paths = Settings.settings.Foods.Convert(f -> f.Images).Reduce(AList::AddAll).ToHashSet();
                    for (File file: Helper.ImageFolder.listFiles())
                        if (!paths.contains(file.getName()))
                            file.delete();
                    for (File file: Helper.TempFolder.listFiles())
                        file.delete();
//                    for (File file: Helper.ExportedDataFolder.listFiles())
//                        file.delete();
                    dialog.dismiss();
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.clear_cache_msg, Toast.LENGTH_SHORT).show();
                    });
                }).start();
            });
            dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        });
        view.findViewById(R.id.export_data_button).setOnClickListener(v -> {
            String filename = Helper.Timestamp() + ".zip", path = Helper.ExportedDataFolder.getPath() + File.separator + filename;
            LoadingDialog dialog = new LoadingDialog();
            dialog.setOnViewCreatedListener(d -> {
                dialog.setMessage(getString(R.string.exporting_data));
                new Thread(() -> {
                    if (Helper.Zip(path, Helper.ImageFolder, new File(Settings.FILENAME))){
                        getActivity().runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), R.string.export_data_msg, Toast.LENGTH_SHORT).show();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                            startActivity(Intent.createChooser(shareIntent, String.format(getString(R.string.share_item), filename)));
                        });
                    }
                }).start();
            });
            dialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        });
        view.findViewById(R.id.save_data_button).setOnClickListener(v -> {
            Helper.Save();
            Toast.makeText(getContext(), R.string.data_saved, Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyFavoritesActivity.REQUEST_CODE && resultCode == RESULT_OK)
            ((MainActivity)getActivity()).randomFragment.Refresh();
    }
}
