package personalprojects.seakyluo.randommenu.controls;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.utils.DeviceUtils;

public class EnhancedBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Setter
    protected boolean showCompatDialogOnFoldableScreen = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (showCompatDialogOnFoldableScreen && DeviceUtils.isFoldableScreen(getContext())){
            return new AppCompatDialog(getContext(), getTheme());
        } else {
            return new BottomSheetDialog(getContext(), getTheme());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet == null){
            return;
        }
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheet.post(() -> {
            // Set peek height to match the bottom sheet's height
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
        });
    }

}
