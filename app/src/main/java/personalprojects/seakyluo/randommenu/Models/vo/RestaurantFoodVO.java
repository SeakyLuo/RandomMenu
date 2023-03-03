package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RestaurantFoodVO implements Parcelable {

    private long id;
    private long restaurantId;
    private long consumeRecordId;
    private String name;
    private String pictureUri;
    private String comment;
    private double price;
    private int orderInHome = -1;

    public void copyFrom(RestaurantFoodVO src){
        id = src.id;
        restaurantId = src.restaurantId;
        consumeRecordId = src.consumeRecordId;
        name = src.name;
        pictureUri = src.pictureUri;
        comment = src.comment;
        price = src.price;
        orderInHome = src.orderInHome;
    }

    protected RestaurantFoodVO(Parcel in) {
        id = in.readLong();
        restaurantId = in.readLong();
        consumeRecordId = in.readLong();
        name = in.readString();
        pictureUri = in.readString();
        comment = in.readString();
        price = in.readDouble();
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
        dest.writeString(name);
        dest.writeString(pictureUri);
        dest.writeString(comment);
        dest.writeDouble(price);
        dest.writeInt(orderInHome);
    }
}