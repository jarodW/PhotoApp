package com.example.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.criminalintent.CrimeDbSchema.*;

/**
 * Created by jarod on 5/19/2017.
 */
//This is a singleton, a class witha  single instance that contains many objects
//OutLive a single fragment and can survive across rotations, across activities, and fragments. They are not a long term solution.
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    Map<UUID,Crime> mMap;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        mMap = new LinkedHashMap<>(); //the <> infers what object type and dose not have to be explicitly stated
        /*for(int i = 0; i < 100; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime : " +  i);
            crime.setSolved(i%2 == 0);
            crime.setRequiresPolice(i%2==0);
            mMap.put(crime.getId(),crime);
        }*/
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.COLS.UUID + "= ?", new String[]{id.toString()});
        Crime crime;
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            else {
                cursor.moveToFirst();
                crime = cursor.getCrime();
            }
        }finally {
            cursor.close();
        }
        return crime;

    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }
        return crimes;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.COLS.UUID + " = ?", new String[]{uuidString});
    }

    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);

    }

    public void deleteCrime(UUID crimeId){
        mDatabase.delete(CrimeTable.NAME,CrimeTable.COLS.UUID + "= ?", new String[]{crimeId.toString()});
        mMap.remove(crimeId);
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.COLS.UUID,crime.getId().toString());
        values.put(CrimeTable.COLS.TITLE,crime.getTitle());
        values.put(CrimeTable.COLS.DATE,crime.getDate().getTime());
        values.put(CrimeTable.COLS.SOLVED,crime.isSolved() ?1 :0);
        values.put(CrimeTable.COLS.SUSPECT,crime.getSuspect());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(CrimeTable.NAME, //Table Name
                null, //Columns
                whereClause, //Where Clause
                whereArgs,// Arguments
                null,//groupBy
                null,//having
                null);//orderBy
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getFilesDir();
        return new File(externalFilesDir, crime.getPhotoFilename());
    }
}
