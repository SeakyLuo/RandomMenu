package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;

public class AskYesNoDialog extends DialogFragment {
    public static final String TAG = "AskYesNoDialog";

    private TextView text_message;
    private Button yes;
    private Button no;
    private Button cancel;
    @Setter
    private View.OnClickListener yesListener, noListener;
    private String message = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_yes_no, container, false);
        text_message = view.findViewById(R.id.ayn_message);
        yes = view.findViewById(R.id.ayn_yes);
        no = view.findViewById(R.id.ayn_no);

        text_message.setText(message);
        yes.setOnClickListener(v -> {
            if (yesListener != null) yesListener.onClick(v);
            dismiss();
        });
        no.setOnClickListener(v -> {
            if (noListener != null) noListener.onClick(v);
            dismiss();
        });
        return view;
    }

    public void setMessage(int message_resource){
        setMessage(getString(message_resource));
    }
    public void setMessage(String message) {
        this.message = message;
        if (text_message != null) text_message.setText(message);
    }
    public void setYesText(String text) { yes.setText(text); }
    public void setNoText(String text) { no.setText(text); }
}
