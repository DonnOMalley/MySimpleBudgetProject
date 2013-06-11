package com.omalleyland.mysimplebudget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by omal310371 on 6/7/13.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Common.DATABASE_NAME, null, Common.DATABASE_VERSION);
        Log.v(getClass().toString(), "DBHelper Constructor");
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.v(getClass().toString(), "Creating Category Table");
        database.execSQL(Common.CREATE_CATEGORY_TABLE);
        Log.v(getClass().toString(), "Creating Store Table");
        database.execSQL(Common.CREATE_STORE_TABLE);
        Log.v(getClass().toString(), "Creating Debit Table");
        database.execSQL(Common.CREATE_DEBIT_TABLE);
        Log.v(getClass().toString(), "Tables Created");
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(getClass().toString(), "Upgrading database version " + Integer.toString(oldVersion) + " => " + Integer.toString(newVersion));
        Log.v(getClass().toString(), "Dropping Category Table");
        database.execSQL(Common.DROP_CATEGORY_TABLE);
        Log.v(getClass().toString(), "Dropping Store Table");
        database.execSQL(Common.DROP_STORE_TABLE);
        Log.v(getClass().toString(), "Dropping Debit Table");
        database.execSQL(Common.DROP_DEBIT_TABLE);
        Log.v(getClass().toString(), "Tables Dropped");
        onCreate(database);
    }


}
