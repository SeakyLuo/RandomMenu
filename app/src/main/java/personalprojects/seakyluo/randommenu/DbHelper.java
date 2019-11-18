package personalprojects.seakyluo.randommenu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase db;
    public static final String TABLE_NAME = "Menu";
    private static ArrayList<DatabaseChangeListener> listeners = new ArrayList<>();

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateDB();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ClearDB();
        CreateDB();
    }

    public static void CreateDB(){
        db.execSQL(String.format("CREATE TABLE {0}()", TABLE_NAME));
    }

    public static void ClearDB(){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        CreateDB();
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
