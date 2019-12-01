package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class SettingsFragment extends Fragment {
    public static final String TAG = "SettingsFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.findViewById(R.id.to_cook_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ToCookActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        });
        view.findViewById(R.id.my_favorites_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyFavoritesActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        });
        view.findViewById(R.id.to_eat_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ToEatActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
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
            Helper.Save(getContext());
            Toast.makeText(getContext(), "Data Adjusted!", Toast.LENGTH_SHORT).show();
        });
        view.findViewById(R.id.clear_data_button).setOnClickListener(v -> {
            Helper.Clear(getContext());
            Toast.makeText(getContext(), "Data Cleared!", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}
