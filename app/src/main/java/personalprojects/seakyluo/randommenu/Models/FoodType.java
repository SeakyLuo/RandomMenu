package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FoodType implements Parcelable {

    private long id;
    private String name;

    protected FoodType(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    public static final Creator<FoodType> CREATOR = new Creator<FoodType>() {
        @Override
        public FoodType createFromParcel(Parcel in) {
            return new FoodType(in);
        }

        @Override
        public FoodType[] newArray(int size) {
            return new FoodType[size];
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
    }

}