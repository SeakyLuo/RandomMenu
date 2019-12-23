package personalprojects.seakyluo.randommenu.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

import personalprojects.seakyluo.randommenu.Helpers.Helper;

public class Food implements Parcelable {
    public String Name;
    public String ImagePath = "";
    public AList<String> Images = new AList<>();
    private AList<Tag> Tags = new AList<>();
    public String Note = "";
    private boolean IsFavorite = false;
    private Long DateAdded;
    public int HideCount = 0;

    public Food(String name){
        Name = name;
    }

    public Food(String name, String path, AList<Tag> tags, String note, boolean isFavorite){
        Name = name;
        ImagePath = path;
        Images.Add(path);
        Tags = tags;
        Note = note;
        IsFavorite = isFavorite;
        DateAdded = Calendar.getInstance().getTimeInMillis();
    }

    public Food Copy(){
        Food food = new Food(Name, ImagePath, Tags, Note, IsFavorite);
        food.Images = new AList<>(Images);
        food.DateAdded = DateAdded;
        return food;
    }

    public boolean SetIsFavorite(boolean isFavorite){ return IsFavorite = isFavorite; }
    public boolean IsFavorite() { return IsFavorite; }

    public boolean HasImage() { return !Helper.IsNullOrEmpty(ImagePath); }

    public boolean HasTag(Tag tag) { return Tags.Contains(tag); }
    public boolean HasTag(String name) { return Tags.Any(t -> t.Name.equals(name)); }
    public void RenameTag(String oldName, String newName){
        Tags.Find(t -> t.Name.equals(oldName)).Name = newName;
    }
    public Long GetDateAdded() { return DateAdded; }
    public AList<Tag> GetTags() { return Tags; }

    public void RemoveTag(Tag tag) { Tags.Remove(tag); }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Food && Name.equals(((Food)obj).Name);
    }

    protected Food(Parcel in) {
        Name = in.readString();
        ImagePath = in.readString();
        ArrayList<String> images = new ArrayList<>();
        in.readStringList(images);
        Images = new AList<>(images);
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
        HideCount = in.readInt();
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
        dest.writeStringList(Images.ToArrayList());
        dest.writeTypedList(Tags.ToArrayList());
        dest.writeString(Note);
        dest.writeByte((byte) (IsFavorite ? 1 : 0));
        if (DateAdded == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(DateAdded);
        }
        dest.writeInt(HideCount);
    }
}
