package personalprojects.seakyluo.randommenu.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import personalprojects.seakyluo.randommenu.Helpers.Helper;

public class Food implements Parcelable {
    public String Name;
    public String ImagePath = "";
    private AList<Tag> Tags = new AList<>();
    public String Note = "";
    private boolean IsFavorite = false;
    private Long DateAdded;

    public Food(String name){
        Name = name;
    }

    public Food(String name, String path, AList<Tag> tags, String note){
        Name = name;
        ImagePath = path;
        Tags = tags;
        Note = note;
        DateAdded = Calendar.getInstance().getTimeInMillis();
    }

    public void SetIsFavorite(boolean isFavorite){
        if (IsFavorite = isFavorite){
            Settings.settings.Favorites.Remove(this);
        }else{
            Settings.settings.Favorites.Add(this);
        }
    }
    public boolean IsFavorite() { return IsFavorite; }

    public boolean HasImage() { return !Helper.IsNullOrEmpty(ImagePath); }

    public boolean HasTag(Tag tag) { return Tags.Contains(tag); }

    public AList<Tag> GetTags() { return Tags; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Food && Name.equals(((Food)obj).Name);
    }

    protected Food(Parcel in) {
        Name = in.readString();
        ImagePath = in.readString();
        ArrayList<Tag> tags = new ArrayList<>();
        in.readTypedList(tags, Tag.CREATOR);
        Tags = new AList<>(tags);
        Note = in.readString();
        IsFavorite = in.readByte() != 0;
        if (in.readByte() == 0) {
            DateAdded = null;
        } else {
            DateAdded = in.readLong();
        }
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
        dest.writeTypedList(Tags.ToArrayList());
        dest.writeString(Note);
        dest.writeByte((byte) (IsFavorite ? 1 : 0));
        if (DateAdded == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(DateAdded);
        }
    }
}
