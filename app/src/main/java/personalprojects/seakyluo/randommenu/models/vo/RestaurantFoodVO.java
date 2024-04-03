package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RestaurantFoodVO implements Parcelable {

    private long id;
    private long restaurantId;
    private long consumeRecordId;
    private int consumeRecordIndex = -1;
    private String name;
    private String cover;
    private List<String> images;
    private String comment;
    private double price;
    private int index = -1;
    private int orderInHome = -1;

    protected RestaurantFoodVO(Parcel in) {
        id = in.readLong();
        restaurantId = in.readLong();
        consumeRecordId = in.readLong();
        consumeRecordIndex = in.readInt();
        name = in.readString();
        cover = in.readString();
        images = in.createStringArrayList();
        comment = in.readString();
        price = in.readDouble();
        index = in.readInt();
        orderInHome = in.readInt();
    }

    public static final Creator<RestaurantFoodVO> CREATOR = new Creator<RestaurantFoodVO>() {
        @Override
        public RestaurantFoodVO createFromParcel(Parcel in) {
            return new RestaurantFoodVO(in);
        }

        @Override
        public RestaurantFoodVO[] newArray(int size) {
            return new RestaurantFoodVO[size];
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
        dest.writeLong(consumeRecordId);
        dest.writeInt(consumeRecordIndex);
        dest.writeString(name);
        dest.writeString(cover);
        dest.writeStringList(images);
        dest.writeString(comment);
        dest.writeDouble(price);
        dest.writeInt(index);
        dest.writeInt(orderInHome);
    }
}