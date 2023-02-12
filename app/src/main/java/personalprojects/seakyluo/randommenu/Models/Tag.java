package personalprojects.seakyluo.randommenu.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Data;
import personalprojects.seakyluo.randommenu.R;

@Data
public class Tag implements Comparable<Tag>, Parcelable {
    public static String AllCategories = "All Categories";
    public static Tag AllCategoriesTag = new Tag(AllCategories);
    public static final int MAX_TAGS = 10;
    public String Name;
    protected int Counter = 0;
    private transient boolean isSelected = false;

    protected Tag() {}
    public Tag(String name){ this.Name = name; }
    public Tag(String name, int counter){
        this.Name = name;
        this.Counter = counter;
    }

    public Tag more() { ++Counter; return this; }
    public Tag less() { --Counter; return this; }
    public boolean isEmpty() { return Counter == 0; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Tag && Name.equals(((Tag)obj).Name);
    }

    public boolean isAllCategoriesTag() { return equals(AllCategoriesTag); }

    @Override
    public int hashCode() {
        return Name.hashCode();
    }

    @NonNull
    @Override
    public String toString() { return Name + " " + Counter; }
    public static String format(Context context, String name, int count){
        return String.format(context.getString(R.string.tag_format), name, count);
    }
    public static String format(Context context, int resId, int count){
        return format(context, context.getString(resId), count);
    }
    public static String format(Context context, Tag tag){
        return format(context, tag.Name, tag.Counter);
    }

    protected Tag(Parcel in) {
        Name = in.readString();
        Counter = in.readInt();
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
        return Counter == tag.Counter ? Name.compareTo(tag.Name) : Counter - tag.Counter ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeInt(Counter);
    }
}


