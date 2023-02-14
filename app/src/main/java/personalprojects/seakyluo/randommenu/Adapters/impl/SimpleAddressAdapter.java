package personalprojects.seakyluo.randommenu.adapters.impl;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.interfaces.AddressOperateListener;
import personalprojects.seakyluo.randommenu.models.Address;

public class SimpleAddressAdapter extends CustomAdapter<Address> {

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_string;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, Address data, int position) {
        View view = viewHolder.getView();
        TextView content = view.findViewById(R.id.text_content);
        content.setText(data.getAddress());
    }
}
