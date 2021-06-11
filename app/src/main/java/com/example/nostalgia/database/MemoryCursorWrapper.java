package com.example.nostalgia.database;
import android.database.Cursor;
import android.database.CursorWrapper;
import com.example.nostalgia.models.Memory;
import com.example.nostalgia.database.MemoryDbSchema.memoryTable;
import java.util.Date;
import java.util.UUID;

public class MemoryCursorWrapper extends CursorWrapper {

    public MemoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Memory getMemory(){
        String uuid = getString(getColumnIndex(memoryTable.Columns.UUID));
        String title = getString(getColumnIndex(memoryTable.Columns.TITLE));
        String detail = getString(getColumnIndex(memoryTable.Columns.DETAIL));
        long date = getLong(getColumnIndex(memoryTable.Columns.DATE));
        String event = getString(getColumnIndex(memoryTable.Columns.EVENT));
        String mediapaths = getString(getColumnIndex(memoryTable.Columns.MEDIAPATH));

        Memory memory = new Memory(UUID.fromString(uuid));
        memory.setTitle(title);
        memory.setDetail(detail);
        memory.setDate(new Date(date));
        memory.setEvent(event);
        memory.setMediaPaths(mediapaths);

        return memory;
    }
}
