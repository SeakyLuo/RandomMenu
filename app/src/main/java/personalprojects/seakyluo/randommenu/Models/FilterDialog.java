package personalprojects.seakyluo.randommenu.Models;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import personalprojects.seakyluo.randommenu.R;

public class FilterDialog extends DialogFragment {
    public static final String TAG = "FilterDialog";
    private Button clear_button;
    private View.OnClickListener clearListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);
//        clear_button = view.findViewById(R.id.clear_button);
//
//        clear_button.setOnClickListener(clearListener);
        return view;
    }
}
