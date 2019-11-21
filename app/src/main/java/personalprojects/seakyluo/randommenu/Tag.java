package personalprojects.seakyluo.randommenu;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Tag implements Comparable, Parcelable {
    public static final String TABLE_NAME = "Tag", COLUMN_NAME = "Name", COLUMN_COUNT = "count";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME +" text PRIMARY KEY)";
    public static final int MAX_TAGS = 10;
    protected String Name;
    protected int Counter = 0;

    protected Tag() {}
    public Tag(String name){
        this.Name = name;
    }

    public String getName() {
        return Name;
    }
    public int More() { return ++Counter; }
    public int Less() { return --Counter; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Tag)) return false;
        return Name.equals(((Tag)obj).Name);
    }

    @NonNull
    @Override
    public String toString() {
        return Name + "(" + Counter + ")";
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
        return Counter > tag.Counter ? Counter - tag.Counter : Name.compareTo(tag.Name);
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


