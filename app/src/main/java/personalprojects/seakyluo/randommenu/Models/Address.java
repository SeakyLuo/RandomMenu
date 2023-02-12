package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Address implements Parcelable {

    private String province;
    private String city;
    private String district;
    private String address;

    protected Address(Parcel in) {
        province = in.readString();
        city = in.readString();
        district = in.readString();
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
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(address);
    }
}
