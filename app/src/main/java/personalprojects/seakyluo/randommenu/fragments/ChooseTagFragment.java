package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.activities.impl.ChooseTagActivity;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

import static android.app.Activity.RESULT_OK;

public class ChooseTagFragment extends Fragment {
    public static final String TAG = "ChooseTagFragment";
    private ImageButton add_tag_button;
    private TextView header_text;
    private TagsFragment tagsFragment = new TagsFragment();
    private String header;
    @Setter
    private Consumer<Intent> chooseTagListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_tag, container, false);
        header_text = view.findViewById(R.id.header_text);
        add_tag_button = view.findViewById(R.id.add_tag_button);

        tagsFragment.setCloseable(true);
        getChildFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment).commit();
        if (StringUtils.isNotEmpty(header)) header_text.setText(header);
        add_tag_button.setOnClickListener(v -> LaunchChooseTagActivity());
        view.findViewById(R.id.tags_frame).setOnClickListener(v -> {
            if (tagsFragment.getData().size() < Tag.MAX_TAGS)
                LaunchChooseTagActivity();
        });
        return view;
    }

    public void setData(List<Tag> data) { tagsFragment.setData(data); }
    public void setData(AList<Tag> data) { tagsFragment.setData(data); }
    public AList<Tag> getData() { return tagsFragment.getData(); }
    public void setHeader(String text) { header = text; if (header_text != null) header_text.setText(text); }

    private void LaunchChooseTagActivity(){
        Intent intent = new Intent(getActivity(), ChooseTagActivity.class);
        intent.putExtra(ChooseTagActivity.SELECTED_TAGS, tagsFragment.getData());
        if (chooseTagListener != null) chooseTagListener.accept(intent);
        startActivityForResult(intent, ActivityCodeConstant.CHOOSE_TAG);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.CHOOSE_TAG){
            ArrayList<Tag> tags = data.getParcelableArrayListExtra(ChooseTagActivity.SELECTED_TAGS);
            setData(tags);
            add_tag_button.setVisibility(tags.size() == Tag.MAX_TAGS ? View.GONE : View.VISIBLE);
        }
    }
}
