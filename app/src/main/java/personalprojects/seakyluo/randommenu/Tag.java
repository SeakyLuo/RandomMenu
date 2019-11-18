package personalprojects.seakyluo.randommenu;

public class Tag {
    public static final String TABLE_NAME = "Tag", COLUME_NAME = "name";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUME_NAME +" text PRIMARY KEY)";
    private String name;

    public String getName() {
        return name;
    }
}
