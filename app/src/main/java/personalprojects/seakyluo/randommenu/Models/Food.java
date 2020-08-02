package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Food implements Parcelable {
    public String Name;
    public AList<String> Images = new AList<>();
    private AList<Tag> Tags = new AList<>();
    public String Note = "";
    private boolean IsFavorite = false;
    private String Cover = "";
    private Long DateAdded = 0L;
    public int HideCount = 0;

    public Food(String name){
        Name = name;
    }

    public Food(String name, AList<String> images, AList<Tag> tags, String note, boolean isFavorite, String cover){
        this(name, images, tags, note, isFavorite, cover, Calendar.getInstance().getTimeInMillis());
    }
    private Food(String name, AList<String> images, AList<Tag> tags, String note, boolean isFavorite, String cover, Long dateAdded){
        Name = name;
        Images = images;
        Tags = tags;
        Note = note;
        IsFavorite = isFavorite;
        Cover = cover;
        DateAdded = dateAdded;
    }

    public Food Copy(){ return new Food(Name, Images.copy(), Tags.copy(), Note, IsFavorite, Cover, DateAdded); }
    public boolean SetIsFavorite(boolean isFavorite){ return IsFavorite = isFavorite; }
    public boolean IsFavorite() { return IsFavorite; }

    public boolean HasImage() { return Images.count() > 0; }
    public boolean HasTag(Tag tag) { return Tags.contains(tag); }
    public boolean HasTag(String name) { return Tags.any(t -> t.Name.equals(name)); }
    public void RenameTag(String oldName, String newName){ Tags.first(t -> t.Name.equals(oldName)).Name = newName; }
    public Long GetDateAdded() { return DateAdded; }
    public String GetDateAddedString(){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(DateAdded);
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }
    public void AddTags(AList<Tag> tags) {
        Tags.addAll(tags);
        tags.forEach(tag -> {
            if (!Tags.contains(tag))
                Tags.add(tag);
        });
    }
    public AList<Tag> GetTags() { return Tags; }
    public void SetCover(String Cover) { this.Cover = Cover; }
    public String GetCover() { return Cover; }
    public void RemoveTag(Tag tag) { Tags.remove(tag); }
    public static boolean IsIncomplete(Food food) { return food == null || food.DateAdded == 0; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Food && Name.equals(((Food)obj).Name);
    }

    protected Food(Parcel in) {
        Name = in.readString();
        ArrayList<String> images = new ArrayList<>();
        in.readStringList(images);
        Images = new AList<>(images);
        ArrayList<Tag> tags = new ArrayList<>();
        in.readTypedList(tags, Tag.CREATOR);
        Tags = new AList<>(tags);
        Note = in.readString();
        IsFavorite = in.readByte() != 0;
        Cover = in.readString();
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
        dest.writeStringList(Images.toList());
        dest.writeTypedList(Tags.toList());
        dest.writeString(Note);
        dest.writeByte((byte) (IsFavorite ? 1 : 0));
        dest.writeString(Cover);
        if (DateAdded == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(DateAdded);
        }
        dest.writeInt(HideCount);
    }
}
