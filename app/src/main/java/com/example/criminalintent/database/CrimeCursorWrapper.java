package com.example.criminalintent.database;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.Crime;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuid = getString(getColumnIndex(CrimeTable.Columns.UUID));
        String title = getString(getColumnIndex(CrimeTable.Columns.TITLE));
        String detail = getString(getColumnIndex(CrimeTable.Columns.DETAIL));
        long date = getLong(getColumnIndex(CrimeTable.Columns.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Columns.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Columns.SUSPECT));
        String number = getString(getColumnIndex(CrimeTable.Columns.NUMBER));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setDetail(detail);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved!=0);
        crime.setSuspect(suspect);
        crime.setNumber(number);

        return crime;
    }
}
