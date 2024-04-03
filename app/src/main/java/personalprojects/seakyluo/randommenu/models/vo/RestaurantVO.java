package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import personalprojects.seakyluo.randommenu.models.FoodType;

@NoArgsConstructor
@Data
public class RestaurantVO implements Parcelable {

    private long id;
    private String name;
    private List<AddressVO> addressList;
    private FoodType foodType;
    private double averageCost;
    private String comment;
    private String link;
    private List<ConsumeRecordVO> records;
    private List<RestaurantFoodVO> foods;

    public double computeAverageCost(){
        if (CollectionUtils.isEmpty(records)) return 0;
        double total = records.stream().mapToDouble(ConsumeRecordVO::getTotalCost).sum();
        double eaterCount = records.stream().filter(i -> i.getTotalCost() != 0).mapToInt(r -> r.getEaters().size() + 1).sum();
        if (eaterCount == 0) return 0;
        return total / eaterCount;
    }

    protected RestaurantVO(Parcel in) {
        id = in.readLong();
        name = in.readString();
        addressList = in.createTypedArrayList(AddressVO.CREATOR);
        foodType = in.readParcelable(FoodType.class.getClassLoader());
        averageCost = in.readDouble();
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeTypedList(addressList);
        dest.writeParcelable(foodType, flags);
        dest.writeDouble(averageCost);
        dest.writeString(comment);
        dest.writeString(link);
        dest.writeTypedList(records);
        dest.writeTypedList(foods);
    }
}
