package personalprojects.seakyluo.randommenu.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelfMadeFood implements Parcelable {
    private long id;
    private String name;
    private List<String> images = new AList<>();
    private List<Tag> tags = new AList<>();
    private String note = "";
    private boolean favorite = false;
    private String cover = "";
    private Long dateAdded = 0L;
    private int hideCount = 0;
    private transient boolean isSelected = false;

    public SelfMadeFood(String name){
        this.name = name;
    }

    public SelfMadeFood(String name, List<String> images, List<Tag> tags, String note, boolean isFavorite, String cover){
        this(name, images, tags, note, isFavorite, cover, System.currentTimeMillis());
    }

    private SelfMadeFood(String name, List<String> images, List<Tag> tags, String note, boolean isFavorite, String cover, Long dateAdded){
        this.name = name;
        this.images = images;
        this.tags = tags;
        this.note = note;
        this.favorite = isFavorite;
        this.cover = cover;
        this.dateAdded = dateAdded;
    }

    public SelfMadeFood copy(){
        return new SelfMadeFood(name, new ArrayList<>(images), new ArrayList<>(tags), note, favorite, cover, dateAdded);
    }

    public boolean hasImage() {
        return images.size() > 0;
    }

    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    public void addTags(List<Tag> src) {
        for (Tag tag : src){
            if (!tags.contains(tag))
                tags.add(tag);
        }
    }
    public String formatDateAdded(){
        return new SimpleDateFormat("yyyy-MM-dd").format(dateAdded);
    }

    protected SelfMadeFood(Parcel in) {
        id = in.readLong();
        name = in.readString();
        images = in.createStringArrayList();
        tags = in.createTypedArrayList(Tag.CREATOR);
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

    public static final Creator<SelfMadeFood> CREATOR = new Creator<SelfMadeFood>() {
        @Override
        public SelfMadeFood createFromParcel(Parcel in) {
            return new SelfMadeFood(in);
        }

        @Override
        public SelfMadeFood[] newArray(int size) {
            return new SelfMadeFood[size];
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

    @Override
    public boolean equals(Object o){
        if (!(o instanceof SelfMadeFood)){
            return false;
        }
        SelfMadeFood food = (SelfMadeFood) o;
        if (id == 0 && food.getId() == 0){
            return StringUtils.equals(name, food.getName());
        }
        return id == food.getId();
    }
}
