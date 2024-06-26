package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.ChooseTagActivity;
import personalprojects.seakyluo.randommenu.controls.EnhancedBottomSheetDialogFragment;
import personalprojects.seakyluo.randommenu.fragments.ChooseTagFragment;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;

public class RandomFilterDialog extends EnhancedBottomSheetDialogFragment {
    public static final String TAG = "FilterDialog";
    private ChooseTagFragment prefer = new ChooseTagFragment(), exclude = new ChooseTagFragment();
    @Setter
    private View.OnClickListener resetListener;
    @Setter
    private BiConsumer<AList<Tag>, AList<Tag>> tagFilterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tag_filter, container, false);
        Button confirm_button = view.findViewById(R.id.doneButton);
        Button reset_button = view.findViewById(R.id.resetButton);

        getChildFragmentManager().beginTransaction().add(R.id.prefer_tags, prefer).add(R.id.exclude_tags, exclude).commit();
        prefer.setHeader(getString(R.string.prefer_tags));
        prefer.setChooseTagListener(intent -> intent.putExtra(ChooseTagActivity.EXCLUDED_TAGS, exclude.getData()));
        exclude.setHeader(getString(R.string.exclude_tags));
        exclude.setChooseTagListener(intent -> intent.putExtra(ChooseTagActivity.EXCLUDED_TAGS, prefer.getData()));
        confirm_button.setOnClickListener(v -> tagFilterListener.accept(prefer.getData(), exclude.getData()));
        reset_button.setOnClickListener(resetListener);

        return view;
    }


    public void setData(AList<Tag> preferred, AList<Tag> excluded){
        prefer.setData(preferred);
        exclude.setData(excluded);
    }

}

