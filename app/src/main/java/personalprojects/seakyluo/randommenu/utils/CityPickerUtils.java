package personalprojects.seakyluo.randommenu.utils;

import com.lljjcoder.citywheel.CityConfig;

import java.util.Optional;

import personalprojects.seakyluo.randommenu.models.vo.AddressVO;

public class CityPickerUtils {

    public static CityConfig buildAddressPicker(AddressVO address){
        return buildCountyPicker(address.getProvince(), address.getCity(), address.getCounty());
    }

    public static CityConfig buildCountyPicker(String province, String city, String district){
        return builder()
                .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS)//显示类，只显示省份一级，显示省市两级还是显示省市区三级
                .province(getDefaultProvince(province))//默认显示的省份
                .city(getDefaultCity(city))//默认显示省份下面的城市
                .district(getDefaultCounty(district))//默认显示省市下面的区县数据
                .build();
    }

    public static CityConfig buildProvincePicker(String province){
        return builder()
                .province(getDefaultProvince(province))
                .setCityWheelType(CityConfig.WheelType.PRO)
                .build();
    }

    public static CityConfig buildCityPicker(String province, String city){
        return builder()
                .province(getDefaultProvince(province))
                .city(getDefaultCity(city))
                .setCityWheelType(CityConfig.WheelType.PRO_CITY)
                .build();
    }

    public static String getDefaultProvince(String province){
        return Optional.ofNullable(province).orElse("上海市");
    }

    public static String getDefaultCity(String city){
        return Optional.ofNullable(city).orElse("上海市");
    }

    public static String getDefaultCounty(String county){
        return Optional.ofNullable(county).orElse("普陀区");
    }

    private static CityConfig.Builder builder(){
        return new CityConfig.Builder()
                .title("选择城市")//标题
                .titleTextSize(18)//标题文字大小
                .titleTextColor("#585858")//标题文字颜  色
                .titleBackgroundColor("#E9E9E9")//标题栏背景色
                .confirTextColor("#585858")//确认按钮文字颜色
                .confirmText("确定")//确认按钮文字
                .confirmTextSize(16)//确认按钮文字大小
                .cancelTextColor("#585858")//取消按钮文字颜色
                .cancelText("取消")//取消按钮文字
                .cancelTextSize(16)//取消按钮文字大小
                .showBackground(true)//是否显示半透明背景
                .visibleItemsCount(7)//显示item的数量
                .provinceCyclic(false)//省份滚轮是否可以循环滚动
                .cityCyclic(false)//城市滚轮是否可以循环滚动
                .districtCyclic(false)//区县滚轮是否循环滚动
                .drawShadows(true)//滚轮不显示模糊效果
                .setLineColor("#03a9f4")//中间横线的颜色
                .setLineHeigh(5)//中间横线的高度
                .setShowGAT(true);//是否显示港澳台数据，默认不显示
    }
}
