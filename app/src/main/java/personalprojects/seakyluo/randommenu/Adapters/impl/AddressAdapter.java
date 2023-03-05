package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.citypickerview.CityPickerView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.DraggableAdapter;
import personalprojects.seakyluo.randommenu.models.AddressVO;
import personalprojects.seakyluo.randommenu.utils.CityPickerUtils;

public class AddressAdapter extends DraggableAdapter<AddressVO> {

    public AddressAdapter(Context context){
        this.context = context;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_address;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, AddressVO data, int position) {
        View view = viewHolder.getView();
        TextView textDistrict = view.findViewById(R.id.text_district);
        EditText textAddress = view.findViewById(R.id.text_address);
        ImageButton reorderButton = view.findViewById(R.id.reorder_button);
        CityPickerView cityPickerView = new CityPickerView();

        cityPickerView.init(context);
        cityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                String provinceName = province.getName();
                data.setProvince(provinceName);
                if (provinceName.endsWith("市")){
                    data.setCity(provinceName);
                } else {
                    data.setCity(city.getName());
                }
                data.setCounty(district.getName());
                textDistrict.setText(data.buildDistrict());
            }
        });
        textDistrict.setOnClickListener(v -> {
            cityPickerView.setConfig(CityPickerUtils.buildConfig(data));
            cityPickerView.showCityPicker();
        });
        textAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                data.setAddress(textAddress.getText().toString());
            }
        });
        fillAddress(data, textDistrict, textAddress);
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

    private void fillAddress(AddressVO data, TextView textDistrict, TextView textAddress){
        textDistrict.setText(data.buildDistrict());
        textAddress.setText(data.getAddress());
    }

}
