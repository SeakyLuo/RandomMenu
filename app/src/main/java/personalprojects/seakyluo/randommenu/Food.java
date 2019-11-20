package personalprojects.seakyluo.randommenu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Food implements Parcelable {
    public String Name;
    public String ImagePath = "";
    private ArrayList<Tag> Tags = new ArrayList<>();
    public String Note = "";
    public Food(String name, String path, ArrayList<Tag> tags, String note){
        Name = name;
        ImagePath = path;
        Tags = tags;
        Note = note;
    }

    public boolean HasImage() { return !ImagePath.equals(""); }

    public boolean HasTag(Tag tag) { return Tags.contains(tag); }

    public ArrayList<Tag> GetTags() { return Tags; }

    protected Food(Parcel in) {
        Name = in.readString();
        ImagePath = in.readString();
        Note = in.readString();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(ImagePath);
        dest.writeString(Note);
    }
}
