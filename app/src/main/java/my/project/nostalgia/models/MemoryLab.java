package my.project.nostalgia.models;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.project.nostalgia.database.MemoryCursorWrapper;
import my.project.nostalgia.database.MemoryBaseHelper;
import my.project.nostalgia.database.MemoryDbSchema.memoryTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores and retrieves memories in a database.
 */
public class MemoryLab {

    //region Declarations
    private static MemoryLab sMemoryLab;
    private SQLiteDatabase mSQLiteDatabase;
    //endregion

    public static MemoryLab get(Context context){
        if(sMemoryLab == null)
            sMemoryLab = new MemoryLab(context);
        return sMemoryLab;
    }

    public void addMemory(Memory memory){
        ContentValues value = getContentValues(memory);
        mSQLiteDatabase.insert(memoryTable.NAME, null, value);
    }

    private MemoryLab(Context context){
        Context context1 = context.getApplicationContext();
        mSQLiteDatabase = new MemoryBaseHelper(context1).getWritableDatabase();
    }

    //region get list of memories
    public List<Memory> getMemories() {
        List<Memory> memories = new ArrayList<>();
        MemoryCursorWrapper ccwrapper = queryMemories(null, null);
        try{
            ccwrapper.moveToFirst();
            while(!ccwrapper.isAfterLast()){
                memories.add(ccwrapper.getMemory());
                ccwrapper.moveToNext();
            }
        } finally {
            ccwrapper.close();
        }
        return memories;
    }
    //endregion

    public void deleteMemory(Memory memory){

        List<Memory> memoriess = this.getMemories();
        mSQLiteDatabase.delete(memoryTable.NAME, memoryTable.Columns.UUID + "= ?", new String[] {memory.getId().toString()} );
    }
    //region get memory from UUID
    public Memory getMemory(UUID id){

        MemoryCursorWrapper ccwrapper = queryMemories(memoryTable.Columns.UUID + "= ?", new String[]{ id.toString()});

        try{
            if(ccwrapper.getCount()==0)
                return null;
            ccwrapper.moveToFirst();
            return ccwrapper.getMemory();
        } finally {
            ccwrapper.close();
        }
    }
    //endregion

    public void updateMemory(Memory memory){

        ContentValues value = getContentValues(memory);
        mSQLiteDatabase.update(memoryTable.NAME, value, memoryTable.Columns.UUID + " = ?"
                , new String[] {memory.getId().toString()});

    }

    private static ContentValues getContentValues(Memory memory){
        ContentValues value = new ContentValues();
        value.put(memoryTable.Columns.UUID, memory.getId().toString());
        value.put(memoryTable.Columns.DATE, memory.getDate().getTime());
        value.put(memoryTable.Columns.EVENT, memory.getEvent());
        value.put(memoryTable.Columns.TITLE, memory.getTitle());
        value.put(memoryTable.Columns.DETAIL, memory.getDetail());
        value.put(memoryTable.Columns.MEDIAPATH, memory.getMediaPaths());

        return value;
    }

    private MemoryCursorWrapper queryMemories(String whereClause, String[] args){
        Cursor cursor = mSQLiteDatabase.query(memoryTable.NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null);

        return new MemoryCursorWrapper(cursor);
    }
}