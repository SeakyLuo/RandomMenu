package personalprojects.seakyluo.randommenu.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import personalprojects.seakyluo.randommenu.ChooseTagActivity;
import personalprojects.seakyluo.randommenu.EditFoodActivity;
import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Interfaces.OnLaunchActivityListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.R;

import static android.app.Activity.RESULT_OK;

public class ChooseTagFragment extends Fragment {
    public static final String TAG = "ChooseTagFragment";
    public static final int CHOOSE_TAG_CODE = 100;
    private ImageButton add_tag_button;
    private TextView header_text;
    private TagsFragment tagsFragment = new TagsFragment();
    private String header;
    private OnLaunchActivityListener chooseTagListener;
    private Food guessTagFood;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_tag, container, false);
        header_text = view.findViewById(R.id.header_text);
        add_tag_button = view.findViewById(R.id.add_tag_button);

        tagsFragment.SetCloseable(true);
        getChildFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment).commit();
        if (!Helper.IsNullOrEmpty(header)) header_text.setText(header);
        add_tag_button.setOnClickListener(v -> LaunchChooseTagActivity());
        view.findViewById(R.id.tag_card_view).setOnClickListener(v -> {
            if (tagsFragment.GetData().Count() < Tag.MAX_TAGS)
                LaunchChooseTagActivity();
        });
        return view;
    }

    public void SetData(List<Tag> data) { tagsFragment.SetData(data); }
    public void SetData(AList<Tag> data) { tagsFragment.SetData(data); }
    public AList<Tag> GetData() { return tagsFragment.GetData(); }
    public void SetHeader(String text) { header = text; if (header_text != null) header_text.setText(text); }
    public void SetChooseTagListener(OnLaunchActivityListener launchActivityListener) { chooseTagListener = launchActivityListener; }

    private void LaunchChooseTagActivity(){
        Intent intent = new Intent(getActivity(), ChooseTagActivity.class);
        intent.putExtra(ChooseTagActivity.SELECTED_TAGS, tagsFragment.GetData().ToArrayList());
        if (Settings.settings.AutoTag){
            intent.putExtra(ChooseTagActivity.FOOD, ((EditFoodActivity)getActivity()).getFoodName());
        }
        if (chooseTagListener != null) chooseTagListener.Launch(intent);
        startActivityForResult(intent, CHOOSE_TAG_CODE);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        ArrayList<Tag> tags = data.getParcelableArrayListExtra(ChooseTagActivity.SELECTED_TAGS);
        SetData(tags);
        add_tag_button.setVisibility(tags.size() == Tag.MAX_TAGS ? View.GONE : View.VISIBLE);
    }
}
