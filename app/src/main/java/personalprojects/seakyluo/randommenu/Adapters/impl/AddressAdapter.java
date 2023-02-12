package personalprojects.seakyluo.randommenu.adapters.impl;

import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
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
        // TODO 只有一个项目的时候不需要排序
        reorderButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (startDragListener != null) startDragListener.requestDrag(viewHolder);
            }
            return false;
        });
    }

    private void fillAddress(TextView textDistrict, TextView textAddress, Address data){
        textDistrict.setText(data.getProvince() + " " + data.getCity() + " " + data.getCounty());
        textAddress.setText(data.getAddress());
    }

}
