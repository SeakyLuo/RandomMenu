package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        view.findViewById(R.id.clear_cache_button).setOnClickListener(v -> {

        });
        view.findViewById(R.id.export_data_button).setOnClickListener(v -> {

        });
        view.findViewById(R.id.adjust_data_button).setOnClickListener(v -> {
//            AList<Tag> tags = new AList<>();
//            Settings.settings.Foods.ForEach(f -> tags.AddAll(f.GetTags()));
//            Settings.settings.Tags.Clear();
//            for (Map.Entry<Tag, Integer> pair: tags.ToHashMap().entrySet())
//                Settings.settings.Tags.Add(new Tag(pair.getKey().Name, pair.getValue()));
//            Settings.settings.SortTags();

//            Settings.settings.Favorites.ForEach(f -> f.SetIsFavorite(true));
//            Settings.settings.Foods.ForEach(f -> f.SetIsFavorite(Settings.settings.Favorites.Contains(f)));
//            Settings.settings.Foods.ForEach(f -> f.Images = new AList<>(f.ImagePath));
//            Settings.settings.Favorites.ForEach(f -> f.Images = new AList<>(f.ImagePath));
//            Settings.settings.Foods.ForEach(f -> f.Images.Remove(Helper::IsNullOrEmpty));
            Helper.Save(getContext());
            Toast.makeText(getContext(), "Data Adjusted!", Toast.LENGTH_SHORT).show();
        });
        view.findViewById(R.id.clear_data_button).setOnClickListener(v -> {
//            Helper.Clear(getContext());
//            HashSet<String> paths = Settings.settings.Foods.Convert(f -> f.ImagePath).Find(s -> !Helper.IsNullOrEmpty(s)).ToHashSet();
//            for (File file: Helper.ImageFolder.listFiles()){
//                if (!paths.contains(file.getPath()))
//                    file.delete();
//            }
//            for (File file: Helper.TempFolder.listFiles())
//                file.delete();
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
