package my.project.nostalgia.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoryBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "memoryBase.db";

    public MemoryBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MemoryDbSchema.memoryTable.NAME + "(" +
                "_id integer primary key autoincrement, "
                + MemoryDbSchema.memoryTable.Columns.UUID + ", "
                + MemoryDbSchema.memoryTable.Columns.TITLE + ", "
                + MemoryDbSchema.memoryTable.Columns.DETAIL + ", "
                + MemoryDbSchema.memoryTable.Columns.DATE + ", "
                + MemoryDbSchema.memoryTable.Columns.EVENT + ", "
                + MemoryDbSchema.memoryTable.Columns.MEDIAPATH +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
