package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;

public class AddressDialog extends DialogFragment {
    public static final String TAG = "AddressDialog";

    @Setter
    private AddressVO address;
    @Setter
    private Consumer<AddressVO> confirmListener;

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

    private void fillAddress(AddressVO address){
        if (address == null){
            return;
        }
        editProvince.setText(address.getProvince());
        editCity.setText(address.getCity());
        editCounty.setText(address.getCounty());
        editAddress.setText(address.getAddress());
    }

    private AddressVO buildAddress(){
        AddressVO data = new AddressVO();
        data.setProvince(editProvince.getText().toString());
        data.setCity(editCity.getText().toString());
        data.setCounty(editCounty.getText().toString());
        data.setAddress(editAddress.getText().toString());
        return data;
    }

    private static final String ZHIXIASHI_REGEX = "(北京|上海|重庆|天津).*";

    private void onConfirm(View view){
        String province = editProvince.getText().toString();
        String city = editCity.getText().toString();
        String county = editCounty.getText().toString();
        boolean isZhixiashi = province.matches(ZHIXIASHI_REGEX) || city.matches(ZHIXIASHI_REGEX); // 直辖市
        if (isZhixiashi){
            if (province.length() > city.length()){
                city = province;
            } else {
                province = city;
            }
            if (!province.endsWith("市")){
                province += "市";
                city += "市";
            }
            editProvince.setText(province);
            editCity.setText(city);
        } else {
            if (province.length() <= 1){
                Toast.makeText(getContext(), "请规范填写省份", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (province.matches("(内蒙古|新疆|广西|宁夏|西藏|香港|澳门)")){

            }
            else if (!province.endsWith("省")){
                editProvince.setText(province + "省");
            }
            if (city.length() <= 1){
                Toast.makeText(getContext(), "请规范填写城市", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!city.endsWith("市")){
                editCity.setText(city + "市");
            }
        }

        if (county.length() <= 1){
            Toast.makeText(getContext(), "请规范填写区县", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isZhixiashi){
            if (!county.endsWith("区")){
                editCounty.setText(county + "区");
            }
        } else if (!county.endsWith("县") && !county.endsWith("区")){
            editCounty.setText(county + "县");
        }

        if (editAddress.getText().length() <= 1){
            Toast.makeText(getContext(), "请规范填写具体地址", Toast.LENGTH_SHORT).show();
            return;
        }
        confirmListener.accept(buildAddress());
        dismiss();
    }
}