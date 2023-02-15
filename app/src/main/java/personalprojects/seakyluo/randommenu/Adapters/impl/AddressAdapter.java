package personalprojects.seakyluo.randommenu.adapters.impl;

import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.interfaces.AddressOperateListener;
import personalprojects.seakyluo.randommenu.models.Address;

public class AddressAdapter extends DraggableAdapter<Address> {

    @Setter
    private AddressOperateListener addressOperateListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_address;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, Address data, int position) {
        View view = viewHolder.getView();
        TextView textDistrict = view.findViewById(R.id.text_district);
        TextView textAddress = view.findViewById(R.id.text_address);
        ImageButton reorderButton = view.findViewById(R.id.reorder_button);

        fillAddress(textDistrict, textAddress, data);
        view.setOnClickListener(v -> {
            AddressDialog dialog = new AddressDialog();
            dialog.setAddress(data);
            dialog.setConfirmListener(address -> {
                data.copyFrom(address);
                fillAddress(textDistrict, textAddress, address);
                addressOperateListener.edited(address, position);
            });
            dialog.showNow(((FragmentActivity)context).getSupportFragmentManager(), AddressDialog.TAG);
        });
        if (getData().size() == 1){
            reorderButton.setVisibility(View.GONE);
        }
        reorderButton.setOnTouchListener((v, event) -> dragStart(viewHolder, event));
    }

    @Override
    protected void dataSizeChanged() {
        for (CustomViewHolder viewHolder : viewHolders){
            viewHolder.getView().findViewById(R.id.reorder_button).setVisibility(data.size() == 1 ? View.GONE : View.VISIBLE);
        }
    }

    private void fillAddress(TextView textDistrict, TextView textAddress, Address data){
        textDistrict.setText(data.buildDistrict());
        textAddress.setText(data.getAddress());
    }

}