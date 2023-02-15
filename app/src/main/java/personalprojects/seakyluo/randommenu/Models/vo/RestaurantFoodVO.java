package personalprojects.seakyluo.randommenu.models.vo;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RestaurantFoodVO implements Parcelable {

    private String name;
    private String pictureUri;
    private String comment;
    private double price;

    public void copyFrom(RestaurantFoodVO src){
        name = src.name;
        pictureUri = src.pictureUri;
        comment = src.comment;
        price = src.price;
    }

    protected RestaurantFoodVO(Parcel in) {
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
        dest.writeString(name);
        dest.writeString(pictureUri);
        dest.writeString(comment);
        dest.writeDouble(price);
    }
}