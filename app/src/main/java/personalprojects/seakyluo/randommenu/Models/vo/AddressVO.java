package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = {"province", "city", "county", "address"})
public class AddressVO implements Parcelable {

    private long id;
    private String province;
    private String city;
    private String county;
    private String address;

    public boolean isEmpty(){
        return StringUtils.isEmpty(province) || StringUtils.isEmpty(city) || StringUtils.isEmpty(city) || StringUtils.isEmpty(address);
    }

    public String buildDistrict(){
        if (province == null){
            return null;
        }
        StringBuilder district = new StringBuilder(province);
        if (!province.equals(city)){
            district.append(city);
        }
        district.append(" ");
        district.append(county);
        return district.toString();
    }

    public String buildFullAddress(){
        StringBuilder sb = new StringBuilder(province);
        if (!province.equals(city)){
            sb.append(city);
        }
        return sb.append(county).append(address).toString();
    }

    public String buildSimpleAddress(){
        if (county == null || address == null) return null;
        return county.endsWith("区") ? county + " " + address : address;
    }

    protected AddressVO(Parcel in) {
        id = in.readLong();
        province = in.readString();
        city = in.readString();
        county = in.readString();
        address = in.readString();
    }

    public static final Creator<AddressVO> CREATOR = new Creator<AddressVO>() {
        @Override
        public AddressVO createFromParcel(Parcel in) {
            return new AddressVO(in);
        }

        @Override
        public AddressVO[] newArray(int size) {
            return new AddressVO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(county);
        dest.writeString(address);
    }
}
