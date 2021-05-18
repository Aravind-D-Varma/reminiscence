package com.example.criminalintent.database;
import android.database.Cursor;
import android.database.CursorWrapper;
import com.example.criminalintent.Memory;
import com.example.criminalintent.database.MemoryDbSchema.memoryTable;
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
        int isSolved = getInt(getColumnIndex(memoryTable.Columns.SOLVED));
        String suspect = getString(getColumnIndex(memoryTable.Columns.SUSPECT));
        String number = getString(getColumnIndex(memoryTable.Columns.NUMBER));

        Memory memory = new Memory(UUID.fromString(uuid));
        memory.setTitle(title);
        memory.setDetail(detail);
        memory.setDate(new Date(date));
        memory.setSolved(isSolved!=0);
        memory.setSuspect(suspect);
        memory.setNumber(number);

        return memory;
    }
}
