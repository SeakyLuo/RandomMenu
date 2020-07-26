package personalprojects.seakyluo.randommenu.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import personalprojects.seakyluo.randommenu.Interfaces.InputConfirmListener;
import personalprojects.seakyluo.randommenu.R;

public class InputDialog extends DialogFragment {
    public static final String TAG = "InputDialog";
    private EditText input_text;
    private Button confirm, cancel;
    private String placeHolder = "", text = "";
    private InputConfirmListener confirmListener;
    private View.OnClickListener cancelListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_input,container,false);
        input_text = view.findViewById(R.id.input_text);
        confirm = view.findViewById(R.id.confirm_button);
        cancel = view.findViewById(R.id.cancel_button);

        input_text.setHint(placeHolder);
        input_text.setText(text);
        input_text.selectAll();
        input_text.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        confirm.setOnClickListener(v -> {
            String text = input_text.getText().toString().trim();
            if (text.length() > 0){
                if (confirmListener != null) confirmListener.Confirm(text);
                dismiss();
            }else{
                input_text.setError("Text cannot be empty!");
            }
        });
        cancel.setOnClickListener(v -> {
            if (cancelListener != null) cancelListener.onClick(v);
            dismiss();
        });
        return view;
    }

    public void SetHint(String placeHolder) { this.placeHolder = placeHolder; if (input_text != null) input_text.setHint(text); }
    public void SetText(String text) {
        this.text = text;
        if (input_text != null){
            input_text.setText(text);
            input_text.selectAll();
        }
    }
    public void SetConfirmListener(InputConfirmListener confirmListener) { this.confirmListener = confirmListener; }
    public void SetCancelListener(View.OnClickListener cancelListener) { this.cancelListener = cancelListener; }

    @Override
    public void dismiss() {
        super.dismiss();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}

