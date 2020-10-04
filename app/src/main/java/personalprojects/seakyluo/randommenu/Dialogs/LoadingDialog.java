package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;

public class LoadingDialog extends DialogFragment {
    public static String TAG = "LoadingDialog";
    public TextView loading_message;
    public ProgressBar loading_progress;
    private View.OnClickListener onViewCreatedListener;
    private Integer message_res_id;
    private String message;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, container,false);
        setCancelable(false);
        loading_message = view.findViewById(R.id.loading_message);
        loading_progress = view.findViewById(R.id.loading_progress);
        if (onViewCreatedListener != null) onViewCreatedListener.onClick(view);
        if (message_res_id != null) setMessage(message_res_id);
        else if (message != null) setMessage(message);
        return view;
    }
    public void setOnViewCreatedListener(View.OnClickListener listener) { onViewCreatedListener = listener; }

    public void setMessage(String message) {
        if (loading_message == null) this.message = message;
        else loading_message.setText(message);
    }
    public void setMessage(int resId) {
        if (loading_message == null) this.message_res_id = resId;
        else loading_message.setText(resId);
    }
    public void setIndeterminate(boolean indeterminate) { loading_progress.setIndeterminate(indeterminate); }
    public void setProgress(int progress) { loading_progress.setProgress(progress); }
    public void setMax(int max) { loading_progress.setMax(max); }
}
