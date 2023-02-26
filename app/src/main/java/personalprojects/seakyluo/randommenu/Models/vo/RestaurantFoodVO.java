package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RestaurantFoodVO implements Parcelable {

    private int id;
    private int consumeRecordId;
    private String name;
    private String pictureUri;
    private String comment;
    private double price;

    public void copyFrom(RestaurantFoodVO src){
        id = src.id;
        consumeRecordId = src.consumeRecordId;
        name = src.name;
        pictureUri = src.pictureUri;
        comment = src.comment;
        price = src.price;
    }

    protected RestaurantFoodVO(Parcel in) {
        id = in.readInt();
        consumeRecordId = in.readInt();
        name = in.readString();
        pictureUri = in.readString();
        comment = in.readString();
        price = in.readDouble();
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
        dest.writeInt(id);
        dest.writeInt(consumeRecordId);
        dest.writeString(name);
        dest.writeString(pictureUri);
        dest.writeString(comment);
        dest.writeDouble(price);
    }
}