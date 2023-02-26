package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Address implements Parcelable {

    private int id;
    private String province;
    private String city;
    private String county;
    private String address;

    public void copyFrom(Address src){
        id = src.id;
        province = src.province;
        city = src.city;
        county = src.county;
        address = src.address;
    }

    public String buildDistrict(){
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
        return county.endsWith("区") ? county + " " + address : address;
    }

    protected Address(Parcel in) {
        id = in.readInt();
        province = in.readString();
        city = in.readString();
        county = in.readString();
        address = in.readString();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(county);
        dest.writeString(address);
    }
}
