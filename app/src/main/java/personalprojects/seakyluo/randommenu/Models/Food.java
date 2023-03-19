package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Food implements Parcelable {
    private long id;
    private String name;
    private AList<String> images = new AList<>();
    private AList<Tag> tags = new AList<>();
    private String note = "";
    private boolean favorite = false;
    private String cover = "";
    private Long dateAdded = 0L;
    private int hideCount = 0;
    private transient boolean isSelected = false;

    public Food(String name){
        this.name = name;
    }

    public Food(String name, AList<String> images, AList<Tag> tags, String note, boolean isFavorite, String cover){
        this(name, images, tags, note, isFavorite, cover, System.currentTimeMillis());
    }

    private Food(String name, AList<String> images, AList<Tag> tags, String note, boolean isFavorite, String cover, Long dateAdded){
        this.name = name;
        this.images = images;
        this.tags = tags;
        this.note = note;
        this.favorite = isFavorite;
        this.cover = cover;
        this.dateAdded = dateAdded;
    }

    public Food copy(){
        return new Food(name, images.copy(), tags.copy(), note, favorite, cover, dateAdded);
    }

    public boolean hasImage() {
        return images.size() > 0;
    }
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    public void addTags(AList<Tag> tags) {
        tags.ForEach(tag -> {
            if (!this.tags.contains(tag))
                this.tags.add(tag);
        });
    }
    public String formatDateAdded(){
        return new SimpleDateFormat("yyyy-MM-dd").format(dateAdded);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Food && name.equals(((Food)obj).name);
    }

    protected Food(Parcel in) {
        id = in.readLong();
        name = in.readString();
        ArrayList<String> images = new ArrayList<>();
        in.readStringList(images);
        this.images = new AList<>(images);
        ArrayList<Tag> tags = new ArrayList<>();
        in.readTypedList(tags, Tag.CREATOR);
        this.tags = new AList<>(tags);
        note = in.readString();
        favorite = in.readByte() != 0;
        cover = in.readString();
        if (in.readByte() == 0) {
            dateAdded = null;
        } else {
            dateAdded = in.readLong();
        }
        hideCount = in.readInt();
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeStringList(images);
        dest.writeTypedList(tags);
        dest.writeString(note);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeString(cover);
        if (dateAdded == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dateAdded);
        }
        dest.writeInt(hideCount);
    }
}
