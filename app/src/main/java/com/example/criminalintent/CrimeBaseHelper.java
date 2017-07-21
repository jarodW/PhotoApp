package com.example.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jarod on 6/1/2017.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeDbSchema.CrimeTable.NAME + "(" +
                "_id integer primary key autoincrement," +
                CrimeDbSchema.CrimeTable.COLS.UUID + "," +
                CrimeDbSchema.CrimeTable.COLS.TITLE + "," +
                CrimeDbSchema.CrimeTable.COLS.DATE + "," +
                CrimeDbSchema.CrimeTable.COLS.SOLVED +"," +
                CrimeDbSchema.CrimeTable.COLS.SUSPECT +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
