package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.interfaces.DataOperationListener;
import personalprojects.seakyluo.randommenu.models.Address;

public class AddressDialog extends DialogFragment {
    public static final String TAG = "AddressDialog";

    @Setter
    private Address address;
    @Setter
    private DataOperationListener<Address> confirmListener;

    private EditText editProvince, editCity, editCounty, editAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_address, container,false);
        editProvince = view.findViewById(R.id.edit_province);
        editCity = view.findViewById(R.id.edit_city);
        editCounty = view.findViewById(R.id.edit_county);
        editAddress = view.findViewById(R.id.edit_address);
        Button confirmButton = view.findViewById(R.id.confirm_button);

        fillAddress(address);
        confirmButton.setOnClickListener(this::onConfirm);
        return view;
    }

    private void fillAddress(Address address){
        if (address == null){
            return;
        }
        editProvince.setText(address.getProvince());
        editCity.setText(address.getCity());
        editCounty.setText(address.getCounty());
        editAddress.setText(address.getAddress());
    }

    private Address buildAddress(){
        Address data = new Address();
        data.setProvince(editProvince.getText().toString());
        data.setCity(editCity.getText().toString());
        data.setCounty(editCounty.getText().toString());
        data.setAddress(editAddress.getText().toString());
        return data;
    }

    private void onConfirm(View view){
        String province = editProvince.getText().toString();
        if (province.length() <= 1){
            Toast.makeText(getContext(), "请填写省份", Toast.LENGTH_SHORT).show();
            return;
        }
        if (province.matches("(北京|上海|重庆|天津)")){
            editProvince.setText(province + "市");
        }
        else if (province.matches("(内蒙古|新疆|广西|宁夏|西藏|香港|澳门)")){

        }
        else if (!province.endsWith("省")){
            editProvince.setText(province + "省");
        }

        if (editCity.getText().length() <= 1){
            if (province.matches("(北京|上海|重庆|天津).*")){
                editCity.setText(province);
            } else{
                Toast.makeText(getContext(), "请填写城市", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!editCity.getText().toString().endsWith("市")){
            editCity.setText(editCity.getText() + "市");
        }

        String county = editCounty.getText().toString();
        if (county.length() <= 1){
            Toast.makeText(getContext(), "请填写区县", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!county.endsWith("县") && !county.endsWith("区")){
            editCounty.setText(county + "县");
        }

        if (editAddress.getText().length() <= 1){
            Toast.makeText(getContext(), "请填写具体地址", Toast.LENGTH_SHORT).show();
            return;
        }
        confirmListener.operate(buildAddress());
        dismiss();
    }
}