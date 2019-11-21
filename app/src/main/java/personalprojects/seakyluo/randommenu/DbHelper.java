package personalprojects.seakyluo.randommenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase db;
    private static DbHelper Instance;
    private static ArrayList<DatabaseChangeListener> listeners = new ArrayList<>();

    public DbHelper GetInstance() { return Instance; }

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db = getWritableDatabase();
    }
    public static void Init(Context context){
        Instance = new DbHelper(context, "RandomMenu.db", null, 1);
    }

    public static File CreateOrOpenFolder(String folderName){
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
        boolean success = folder.exists() || folder.mkdirs();
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
        return folder;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ClearDB(db);
        CreateDB(db);
    }

    public static void CreateDB(SQLiteDatabase db){
        db.execSQL(User.CREATE_TABLE);
        db.execSQL(Tag.CREATE_TABLE);
    }

    public static void UpdateUser(User user){
        db.execSQL("UPDATE " + User.TABLE_NAME +
                   " SET user_json = " + user.toString() +
                   "WHERE user_id = " + user.Id);
        NotifyListeners();
    }

    public static User GetUser(User user){
        String query = "SELECT user_data FROM " + User.TABLE_NAME + "WHERE user_id = " + user.Id;
        Cursor cursor = db.rawQuery(query, null);
        User found = User.FromJson(cursor.getString(cursor.getColumnIndex(User.COLUMN_USER_DATA)));
        cursor.close();
        return found;
    }

    public static void Insert(Object obj){
        ContentValues values = new ContentValues();
        if (obj instanceof User){
            User user = (User)obj;
            values.put("user_id", user.Id);
            values.put("user_data", user.toString());
            db.insert(User.TABLE_NAME, null, values);
        }
        else if (obj instanceof Tag){
            Tag tag = (Tag)obj;
            values.put("Name", tag.getName());
            db.insert(Tag.TABLE_NAME, null, values);
        }
    }

    public static void ClearDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Tag.TABLE_NAME);
        CreateDB(db);
        NotifyListeners();
    }

    public static void Subscribe(DatabaseChangeListener listener){
        listeners.add(listener);
    }

    private static void NotifyListeners(){
        for (DatabaseChangeListener listener: listeners) {
            listener.OnChange();
        }
    }

    public interface DatabaseChangeListener {
        void OnChange();
    }
}
