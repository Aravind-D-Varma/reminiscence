package com.example.criminalintent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminalintent.database.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeCursorWrapper;
import com.example.criminalintent.database.CrimeDbSchema;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    //region Declarations
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    //endregion

    public static CrimeLab get(Context context){
        if(sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    public void addCrime(Crime crime){
        ContentValues value = getContentValues(crime);
        mSQLiteDatabase.insert(CrimeTable.NAME, null, value);
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        //mCrimes = new ArrayList<>();
    }

    //region get list of crimes
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper ccwrapper = queryCrimes(null, null);
        try{
            ccwrapper.moveToFirst();
            while(!ccwrapper.isAfterLast()){
                crimes.add(ccwrapper.getCrime());
                ccwrapper.moveToNext();
            }
        } finally {
            ccwrapper.close();
        }
        return crimes;
    }
    //endregion

    public File getPhotoFile(Crime crime){
        File filesDr = mContext.getFilesDir();
        return new File(filesDr, crime.getPhotoFileName());
    }

    public void deleteCrime(Crime crime){

        List<Crime> crimes = this.getCrimes();
        mSQLiteDatabase.delete(CrimeTable.NAME, CrimeTable.Columns.UUID + "= ?", new String[] {crime.getId().toString()} );
    }
    //region get crime from UUID
    public Crime getCrime(UUID id){

        CrimeCursorWrapper ccwrapper = queryCrimes(CrimeTable.Columns.UUID + "= ?", new String[]{ id.toString()});

        try{
            if(ccwrapper.getCount()==0)
                return null;
            ccwrapper.moveToFirst();
            return ccwrapper.getCrime();
        } finally {
            ccwrapper.close();
        }
    }
    //endregion

    public void updateCrime(Crime crime){

        ContentValues value = getContentValues(crime);
        mSQLiteDatabase.update(CrimeTable.NAME, value, CrimeTable.Columns.UUID + " = ?"
                , new String[] {crime.getId().toString()});

    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues value = new ContentValues();
        value.put(CrimeTable.Columns.UUID, crime.getId().toString());
        value.put(CrimeTable.Columns.DATE, crime.getDate().getTime());
        value.put(CrimeTable.Columns.SOLVED, crime.isSolved()?1:0);
        value.put(CrimeTable.Columns.TITLE, crime.getTitle());
        value.put(CrimeTable.Columns.SUSPECT, crime.getSuspect());
        value.put(CrimeTable.Columns.NUMBER, crime.getNumber());
        value.put(CrimeTable.Columns.DETAIL, crime.getDetail());

        return value;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] args){
        Cursor cursor = mSQLiteDatabase.query(CrimeTable.NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null);

        return new CrimeCursorWrapper(cursor);
    }
}
