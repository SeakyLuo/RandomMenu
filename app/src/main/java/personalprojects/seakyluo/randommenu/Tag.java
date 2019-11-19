package personalprojects.seakyluo.randommenu;

public class Tag {
    public static final String TABLE_NAME = "Tag", COLUMN_NAME = "name";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME +" text PRIMARY KEY)";
    private String name;

    public Tag(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
