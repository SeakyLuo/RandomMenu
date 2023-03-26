package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ConsumeRecordVO implements Parcelable {

    public static final String CONSUME_TIME_FORMAT = "yyyy-MM-dd HH:mm", CONSUME_TIME_FORMAT_IN_DAY = "yyyy-MM-dd";

    private long id;
    private long restaurantId;
    private long consumeTime;
    private AddressVO address;
    private List<String> eaters;
    private double totalCost;
    private String comment;
    private List<RestaurantFoodVO> foods;
    private int index = -1;
    private List<String> environmentPictures;

    public String formatConsumeTime(){
        return DateFormatUtils.format(consumeTime, CONSUME_TIME_FORMAT);
    }

    public String formatConsumeTimeToDay(){
        return DateFormatUtils.format(consumeTime, CONSUME_TIME_FORMAT_IN_DAY);
    }

    public void setIndex(int index){
        this.index = index;
        for (RestaurantFoodVO food : foods){
            food.setConsumeRecordIndex(index);
        }
    }

    protected ConsumeRecordVO(Parcel in) {
        id = in.readLong();
        restaurantId = in.readLong();
        consumeTime = in.readLong();
        address = in.readParcelable(AddressVO.class.getClassLoader());
        eaters = in.createStringArrayList();
        totalCost = in.readDouble();
        comment = in.readString();
        foods = in.createTypedArrayList(RestaurantFoodVO.CREATOR);
        index = in.readInt();
        environmentPictures = in.createStringArrayList();
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
        dest.writeLong(restaurantId);
        dest.writeLong(consumeTime);
        dest.writeParcelable(address, flags);
        dest.writeStringList(eaters);
        dest.writeDouble(totalCost);
        dest.writeString(comment);
        dest.writeTypedList(foods);
        dest.writeInt(index);
        dest.writeStringList(environmentPictures);
    }
}
