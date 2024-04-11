package personalprojects.seakyluo.randommenu.controls;

import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EnhancedBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheet.post(() -> {
            // Set peek height to match the bottom sheet's height
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
        });
    }

}
