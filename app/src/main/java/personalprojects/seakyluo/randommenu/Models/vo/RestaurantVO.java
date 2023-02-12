package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import personalprojects.seakyluo.randommenu.models.Address;

@NoArgsConstructor
@Data
public class RestaurantVO implements Parcelable {

    private String name;
    private List<Address> addressList;
    private String foodTypeCode;
    private String foodTypeName;
    private String comment;
    private String link;
    private List<ConsumeRecordVO> records;
    private List<RestaurantFoodVO> foods;

    protected RestaurantVO(Parcel in) {
        name = in.readString();
        addressList = in.createTypedArrayList(Address.CREATOR);
        foodTypeCode = in.readString();
        foodTypeName = in.readString();
        comment = in.readString();
        link = in.readString();
        records = in.createTypedArrayList(ConsumeRecordVO.CREATOR);
        foods = in.createTypedArrayList(RestaurantFoodVO.CREATOR);
    }

    public static final Creator<RestaurantVO> CREATOR = new Creator<RestaurantVO>() {
        @Override
        public RestaurantVO createFromParcel(Parcel in) {
            return new RestaurantVO(in);
        }

        @Override
        public RestaurantVO[] newArray(int size) {
            return new RestaurantVO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(addressList);
        dest.writeString(foodTypeCode);
        dest.writeString(foodTypeName);
        dest.writeString(comment);
        dest.writeString(link);
        dest.writeTypedList(records);
        dest.writeTypedList(foods);
    }
}
