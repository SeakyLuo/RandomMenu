package personalprojects.seakyluo.randommenu.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Tag implements Comparable, Parcelable {
    public static final String TABLE_NAME = "Tag", COLUMN_NAME = "Name", COLUMN_COUNT = "count";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME +" text PRIMARY KEY," + COLUMN_COUNT + " integer)";
    public static String AllCategories = "All Categories";
    public static Tag AllCategoriesTag = new Tag(AllCategories);
    public static final int MAX_TAGS = 10;
    public String Name;
    protected int Counter = 0;

    protected Tag() {}
    public Tag(String name){ this.Name = name; }
    public Tag(String name, int counter){
        this.Name = name;
        this.Counter = counter;
    }

    public Tag More() { ++Counter; return this; }
    public Tag Less() { --Counter; return this; }

    public boolean IsEmpty() { return Counter == 0; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Tag && Name.equals(((Tag)obj).Name);
    }

    public boolean IsAllCategoriesTag() { return equals(AllCategoriesTag); }

    @Override
    public int hashCode() {
        return Name.hashCode();
    }

    @NonNull
    @Override
    public String toString() { return Format(Name, Counter); }
    public static String Format(String name, int count){
        return name + "(" + count + ")";
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
    public int compareTo(Object o) {
        Tag tag = (Tag)o;
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


