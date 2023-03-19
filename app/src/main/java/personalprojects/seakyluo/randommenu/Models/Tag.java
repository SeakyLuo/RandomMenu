package personalprojects.seakyluo.randommenu.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;
import personalprojects.seakyluo.randommenu.R;

@NoArgsConstructor
@Data
public class Tag implements Comparable<Tag>, Parcelable {
    public static String AllCategories = "All Categories";
    public static Tag AllCategoriesTag = new Tag(AllCategories);
    public static final int MAX_TAGS = 10;
    private long id;
    private String name;
    protected int counter = 0;
    private transient boolean isSelected = false;

    public Tag(String name){ this.name = name; }
    public Tag(String name, int counter){
        this.name = name;
        this.counter = counter;
    }

    public Tag more() { ++counter; return this; }
    public Tag less() { --counter; return this; }
    public boolean isEmpty() { return counter == 0; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Tag && name.equals(((Tag)obj).name);
    }

    public boolean isAllCategoriesTag() { return equals(AllCategoriesTag); }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @NonNull
    @Override
    public String toString() { return name + " " + counter; }

    public static String format(Context context, String name, long count){
        return String.format(context.getString(R.string.tag_format), name, count);
    }

    public static String format(Context context, int resId, long count){
        return format(context, context.getString(resId), count);
    }

    public static String format(Context context, Tag tag){
        return format(context, tag.name, tag.counter);
    }

    protected Tag(Parcel in) {
        id = in.readLong();
        name = in.readString();
        counter = in.readInt();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int compareTo(Tag tag) {
        return counter == tag.counter ? name.compareTo(tag.name) : counter - tag.counter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(counter);
    }
}


