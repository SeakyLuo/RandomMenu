package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import personalprojects.seakyluo.randommenu.models.Address;

@NoArgsConstructor
@Data
public class ConsumeRecordVO implements Parcelable {

    public static final String CONSUME_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private long id;
    private long consumeTime;
    private Address address;
    private List<String> eaters;
    private double totalCost;
    private String comment;
    private List<RestaurantFoodVO> foods;
    private int index = -1;

    public String formatConsumeTime(){
        return DateFormatUtils.format(consumeTime, CONSUME_TIME_FORMAT);
    }

    protected ConsumeRecordVO(Parcel in) {
        id = in.readLong();
        consumeTime = in.readLong();
        address = in.readParcelable(Address.class.getClassLoader());
        eaters = in.createStringArrayList();
        totalCost = in.readDouble();
        comment = in.readString();
        foods = in.createTypedArrayList(RestaurantFoodVO.CREATOR);
        index = in.readInt();
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
        dest.writeLong(id);
        dest.writeLong(consumeTime);
        dest.writeParcelable(address, flags);
        dest.writeStringList(eaters);
        dest.writeDouble(totalCost);
        dest.writeString(comment);
        dest.writeTypedList(foods);
        dest.writeInt(index);
    }
}
