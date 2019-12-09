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
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;

import static android.app.Activity.RESULT_OK;

public class ChooseTagFragment extends Fragment {
    public static final int CHOOSE_TAG_CODE = 100;
    private ImageButton add_tag_button;
    private TextView header_text;
    private TagsFragment tagsFragment = new TagsFragment();
    private String header;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_tag, container, false);
        header_text = view.findViewById(R.id.header_text);
        add_tag_button = view.findViewById(R.id.add_tag_button);

        tagsFragment.SetCloseable(true);
        getFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment).commit();
        if (!Helper.IsNullOrEmpty(header)) header_text.setText(header);
        add_tag_button.setOnClickListener(v -> LaunchChooseTagActivity());
        view.findViewById(R.id.tag_card_view).setOnClickListener(v -> {
            if (tagsFragment.GetData().Count() < Tag.MAX_TAGS)
                LaunchChooseTagActivity();
        });
        return view;
    }

    public void SetData(ArrayList<Tag> data) { tagsFragment.SetData(data); }
    public void SetData(AList<Tag> data) { tagsFragment.SetData(data); }
    public AList<Tag> GetData() { return tagsFragment.GetData(); }
    public void SetHeader(String text) { header = text; if (header_text != null) header_text.setText(text); }

    private void LaunchChooseTagActivity(){
        Intent intent = new Intent(getActivity(), ChooseTagActivity.class);
        intent.putExtra(ChooseTagActivity.TAG, tagsFragment.GetData().ToArrayList());
        startActivityForResult(intent, CHOOSE_TAG_CODE);
//        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        ArrayList<Tag> tags = data.getParcelableArrayListExtra(ChooseTagActivity.TAG);
        SetData(tags);
        add_tag_button.setVisibility(tags.size() == Tag.MAX_TAGS ? View.GONE : View.VISIBLE);
    }
}
