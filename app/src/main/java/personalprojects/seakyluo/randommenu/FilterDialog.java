package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import personalprojects.seakyluo.randommenu.ChooseTagFragment;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.TagFilterListener;
import personalprojects.seakyluo.randommenu.R;

public class FilterDialog extends DialogFragment {
    public static final String TAG = "FilterDialog";
    private Button confirm_button, reset_button;
    private ChooseTagFragment prefer = new ChooseTagFragment(), exclude = new ChooseTagFragment();
    private View.OnClickListener resetListener;
    private TagFilterListener tagFilterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        confirm_button = view.findViewById(R.id.confirm_button);
        reset_button = view.findViewById(R.id.reset_button);

        getChildFragmentManager().beginTransaction().add(R.id.prefer_tags, prefer).add(R.id.exclude_tags, exclude).commit();
        prefer.SetHeader("Prefer Tags");
        prefer.SetChooseTagListener(intent -> intent.putExtra(ChooseTagActivity.EXCLUDED_TAGS, exclude.GetData().ToArrayList()));
        exclude.SetHeader("Exclude Tags");
        exclude.SetChooseTagListener(intent -> intent.putExtra(ChooseTagActivity.EXCLUDED_TAGS, prefer.GetData().ToArrayList()));
        confirm_button.setOnClickListener(v -> tagFilterListener.Filter(prefer.GetData(), exclude.GetData()));
        reset_button.setOnClickListener(resetListener);
        return view;
    }

    public void SetData(AList<Tag> preferred, AList<Tag> excluded){
        prefer.SetData(preferred);
        exclude.SetData(excluded);
    }
    public void SetTagFilterListener(TagFilterListener listener) { tagFilterListener = listener; }
    public void SetOnResetListener(View.OnClickListener listener) { resetListener = listener; }
}

interface OnLaunchActivityListener{
    void Launch(Intent intent);
}