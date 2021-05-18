package com.example.nostalgia.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nostalgia.database.MemoryDbSchema.memoryTable;

public class MemoryBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "memoryBase.db";

    public MemoryBaseHelper (Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + memoryTable.NAME + "(" +
                "_id integer primary key autoincrement, "
                + memoryTable.Columns.UUID + ", "
                + memoryTable.Columns.TITLE + ", "
                + memoryTable.Columns.DETAIL + ", "
                + memoryTable.Columns.DATE + ", "
                + memoryTable.Columns.EVENT + ", "
                + memoryTable.Columns.SUSPECT + ","
                + memoryTable.Columns.NUMBER +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
