package com.example.android.spiceshop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Carina on 2018-01-10.
 */

public class SpiceDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spices.db";

    private static String SQL_CREATE_SPICES_TABLE = "CREATE TABLE " + SpiceContract.Spices.TABLE_NAME + " ("
            + SpiceContract.Spices._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SpiceContract.Spices.COLUMN_SPICE_NAME + " TEXT NOT NULL, "
            + SpiceContract.Spices.COLUMN_SPICE_DESC + " TEXT, "
            + SpiceContract.Spices.COLUMN_SPICE_PRICE + " TEXT, "
            + SpiceContract.Spices.COLUMN_SPICE_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + SpiceContract.Spices.COLUMN_SPICE_IMAGE + " TEXT, "
            + SpiceContract.Spices.COLUMN_SPICE_SUPPLIER + " TEXT);";

    public SpiceDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_SPICES_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){   }

}
