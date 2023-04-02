package personalprojects.seakyluo.randommenu.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.models.RestaurantFilter;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.utils.CityPickerUtils;

public class RestaurantFilterDialog extends BottomSheetDialogFragment {
    public static final String TAG = "RestaurantFilterDialog";
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private TextView provinceTextView, cityTextView, countyTextView, startTimeTextView, endTimeTextView;
    private CityPickerDialog cityPicker;
    @Setter
    private RestaurantFilter restaurantFilter;
    @Setter
    private Consumer<RestaurantFilter> confirmListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_filter, container, false);
        provinceTextView = view.findViewById(R.id.provinceTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        countyTextView = view.findViewById(R.id.countyTextView);
        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        endTimeTextView = view.findViewById(R.id.endTimeTextView);
        Button resetButton = view.findViewById(R.id.resetButton);
        Button doneButton = view.findViewById(R.id.doneButton);

        if (restaurantFilter == null){
            restaurantFilter = new RestaurantFilter();
        }
        cityPicker = new CityPickerDialog();
        cityPicker.init(getContext());
        provinceTextView.setOnClickListener(v -> showProvincePicker());
        cityTextView.setOnClickListener(v -> showCityPicker());
        countyTextView.setOnClickListener(v -> showCountyPicker());
        startTimeTextView.setOnClickListener(v -> {
            showDatePicker(restaurantFilter.getStartTime(), (picker, year, month, dayOfMonth) -> {
                long time = getTimestamp(year, month, dayOfMonth);
                restaurantFilter.setStartTime(time);
                startTimeTextView.setText(DateFormatUtils.format(time, DATE_FORMAT));
            });
        });
        endTimeTextView.setOnClickListener(v -> {
            showDatePicker(restaurantFilter.getEndTime(), (picker, year, month, dayOfMonth) -> {
                long time = getTimestamp(year, month, dayOfMonth);
                restaurantFilter.setEndTime(time);
                endTimeTextView.setText(DateFormatUtils.format(time, DATE_FORMAT));
            });
        });
        resetButton.setOnClickListener(this::reset);
        doneButton.setOnClickListener(this::confirm);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                AddressVO address = Optional.ofNullable(restaurantFilter.getAddress()).orElse(new AddressVO());
                if (province != null && StringUtils.isNotEmpty(province.getName())){
                    address.setProvince(province.getName());
                    provinceTextView.setText(province.getName());
                }
                if (city != null && StringUtils.isNotEmpty(city.getName())){
                    if (address.getProvince().endsWith("市")){
                        address.setCity(address.getProvince());
                        cityTextView.setText(address.getProvince());
                    } else {
                        address.setCity(city.getName());
                        cityTextView.setText(city.getName());
                    }
                }
                if (district != null && StringUtils.isNotEmpty(district.getName())){
                    address.setCounty(district.getName());
                    countyTextView.setText(district.getName());
                }
                restaurantFilter.setAddress(address);
            }
        });
        fillWithRestaurantFilter(restaurantFilter);
        return view;
    }

    private void showProvincePicker(){
        AddressVO address = Optional.ofNullable(restaurantFilter.getAddress()).orElse(new AddressVO());
        if (StringUtils.isNotEmpty(address.getCounty())){
            cityPicker.setConfig(CityPickerUtils.buildCountyPicker(address.getProvince(), address.getCity(), address.getCounty()));
        }
        else if (StringUtils.isNotEmpty(address.getCity())){
            cityPicker.setConfig(CityPickerUtils.buildCityPicker(address.getProvince(), address.getCity()));
        }
        else {
            cityPicker.setConfig(CityPickerUtils.buildProvincePicker(address.getProvince()));
        }
        cityPicker.showNow(getChildFragmentManager(), CityPickerDialog.TAG);
    }

    private void showCityPicker(){
        AddressVO address = Optional.ofNullable(restaurantFilter.getAddress()).orElse(new AddressVO());
        if (StringUtils.isNotEmpty(address.getCounty())){
            cityPicker.setConfig(CityPickerUtils.buildCountyPicker(address.getProvince(), address.getCity(), address.getCounty()));
        }
        else {
            cityPicker.setConfig(CityPickerUtils.buildCityPicker(address.getProvince(), address.getCity()));
        }
        cityPicker.showNow(getChildFragmentManager(), CityPickerDialog.TAG);
    }

    private void showCountyPicker(){
        AddressVO address = Optional.ofNullable(restaurantFilter.getAddress()).orElse(new AddressVO());
        cityPicker.setConfig(CityPickerUtils.buildCountyPicker(address.getProvince(), address.getCity(), address.getCounty()));
        cityPicker.showNow(getChildFragmentManager(), CityPickerDialog.TAG);
    }

    private void showDatePicker(Long initialTime, DatePickerDialog.OnDateSetListener listener){
        DatePickerDialog dialog = new DatePickerDialog(getContext());
        dialog.setOnDateSetListener(listener);
        if (initialTime != null){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(initialTime);
            dialog.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }
        dialog.show();
    }

    private long getTimestamp(int year, int month, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        return c.getTimeInMillis();
    }

    private void reset(View view){
        fillWithRestaurantFilter(null);
        restaurantFilter = new RestaurantFilter();
    }

    private void confirm(View view){
        if (confirmListener != null){
            confirmListener.accept(restaurantFilter);
        }
        dismiss();
    }

    private void fillWithRestaurantFilter(RestaurantFilter filter){
        if (filter == null){
            provinceTextView.setText("省");
            cityTextView.setText("市");
            countyTextView.setText("区");
            startTimeTextView.setText("开始时间");
            endTimeTextView.setText("结束时间");
        } else {
            AddressVO address = filter.getAddress();
            if (address != null){
                if (address.getProvince() != null){
                    provinceTextView.setText(address.getProvince());
                }
                if (address.getCity() != null){
                    cityTextView.setText(address.getCity());
                }
                if (address.getCounty() != null){
                    countyTextView.setText(address.getCounty());
                }
            }
            if (filter.getStartTime() != null){
                startTimeTextView.setText(DateFormatUtils.format(filter.getStartTime(), DATE_FORMAT));
            }
            if (filter.getEndTime() != null){
                endTimeTextView.setText(DateFormatUtils.format(filter.getEndTime(), DATE_FORMAT));
            }
        }
    }

}
