package personalprojects.seakyluo.randommenu.Models;

import java.util.ArrayList;

public class User {
    public static final String TABLE_NAME = "User", COLUMN_USER_ID = "user_id",
                                 COLUMN_USER_DATA = "user_data";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                                                + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                                                + COLUMN_USER_DATA + " JSON)";
    public int Id;
    public ArrayList<Food> Foods = new ArrayList<>();

    public static User FromJson(String json){
        return null;
    }
}
