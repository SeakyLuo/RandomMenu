package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import personalprojects.seakyluo.randommenu.models.Address;

@NoArgsConstructor
@Data
public class ConsumeRecordVO implements Parcelable {

    private Long consumeTime;
    private Address address;
    private List<String> eaters;
    private List<RestaurantFoodVO> foods;

    protected ConsumeRecordVO(Parcel in) {
        if (in.readByte() == 0) {
            consumeTime = null;
        } else {
            consumeTime = in.readLong();
        }
        address = in.readParcelable(Address.class.getClassLoader());
        eaters = in.createStringArrayList();
        foods = in.createTypedArrayList(RestaurantFoodVO.CREATOR);
    }

    public static final Creator<ConsumeRecordVO> CREATOR = new Creator<ConsumeRecordVO>() {
        @Override
        public ConsumeRecordVO createFromParcel(Parcel in) {
            return new ConsumeRecordVO(in);
        }

        @Override
        public ConsumeRecordVO[] newArray(int size) {
            return new ConsumeRecordVO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (consumeTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(consumeTime);
        }
        dest.writeParcelable(address, flags);
        dest.writeStringList(eaters);
        dest.writeTypedList(foods);
    }
}
