package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Tag implements Comparable {
    public static final String TABLE_NAME = "Tag", COLUMN_NAME = "name", COLUMN_COUNT = "count";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME +" text PRIMARY KEY)";
    private String name;
    private int counter = 0;

    protected Tag() {}
    public Tag(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public int More() { return ++counter; }
    public int Less() { return --counter; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Tag)) return false;
        return name.equals(((Tag)obj).name);
    }

    @NonNull
    @Override
    public String toString() {
        return name + "(" + counter + ")";
    }

    @Override
    public int compareTo(Object o) {
        Tag tag = (Tag)o;
        return counter > tag.counter ? counter - tag.counter : name.compareTo(tag.name);
    }
}


