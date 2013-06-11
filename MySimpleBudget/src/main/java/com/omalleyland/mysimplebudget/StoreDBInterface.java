package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/7/13.
 */
public class StoreDBInterface {

    private final Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String className;

    public StoreDBInterface(Context ctx) {
        className = getClass().toString();
        Log.v(className, "StoreDB(Context) Constructor");
        this.context = ctx;
        dbHelper = new DBHelper(this.context);
    }

    private void openDB() throws SQLException {
        Log.v(className, "Opening Database");
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        Log.v(className, "Closing Database");
        dbHelper.close();
        db = null;
    }

    private Store cursorToStore(Cursor cursor) {
        Log.v(className, "Writing Cursor to Store Object");
        Store store = new Store();
        store.setID(cursor.getInt(Common.colSTORE_ID_INDEX));
        store.setStoreName(cursor.getString(Common.colSTORE_NAME_INDEX));
        store.setServerID(cursor.getInt(Common.colSTORE_SERVER_ID_INDEX));
        store.setSyncStatus(cursor.getInt(Common.colSTORE_SYNC_STATUS_INDEX));
        store.setActiveStatus(cursor.getInt(Common.colSTORE_ACTIVE_STATUS_INDEX));
        return store;
    }

    public long addStore(Store store) {
        long insertId = -1;
        Log.d(className, "Adding Store To Database :: id = " + Integer.toString(store.getID()) +
                ", name = " + store.getStoreName() +
                ", serverID = " + Integer.toString(store.getServerID()) +
                ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                ", activeStatus = " + Integer.toString(store.getActiveStatus()));
        try {
            openDB();
            ContentValues values = new ContentValues();
            if(store.getID() > -1) {
                values.put(Common.colSTORE_ID, store.getID());
            }
            values.put(Common.colSTORE_NAME, store.getStoreName());
            if(store.getServerID() > -1) {
                values.put(Common.colSTORE_SERVER_ID, store.getServerID());
            }
            values.put(Common.colSTORE_SYNC_STATUS, store.getSyncStatus());
            values.put(Common.colSTORE_ACTIVE_STATUS, store.getActiveStatus());
            Log.v(className, "Inserting into Store Table");
            insertId = db.insert(Common.tblSTORES, null, values);
        }
        catch (SQLException e) {
            Log.e(className, "Exception Adding Store :: name = '" + store.getStoreName() + "' :: " + e.getMessage());
        }

        closeDB();
        Log.d(className, "StoreID from Insert = " + Long.toString(insertId));
        return insertId;
    }

    public void deleteStore(Store store) {
        Log.d(className, "Deleting Store From Database :: id = " + Integer.toString(store.getID()) +
                ", name = " + store.getStoreName() +
                ", serverID = " + Integer.toString(store.getServerID()) +
                ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                ", activeStatus = " + Integer.toString(store.getActiveStatus()));
        try {
            openDB();
            Log.v(className, "Performing Delete From Store Table");
            db.delete(Common.tblSTORES, Common.colSTORE_NAME + "=?", new String[] { store.getStoreName() });
        }
        catch (Exception e) {
            Log.e(className, "Exception Removing Store :: name = '" + store.getStoreName() + "' :: " + e.getMessage());
        }
        closeDB();
    }

    public Store getStore(int id) {
        Store store = new Store();
        try {
            openDB();
            Log.v(className, "Querying Store by ID = " + Integer.toString(id));
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, Common.colSTORE_ID + " = " + id, null, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                store = cursorToStore(cursor);
                Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                        ", name = " + store.getStoreName() +
                        ", serverID = " + Integer.toString(store.getServerID()) +
                        ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                        ", activeStatus = " + Integer.toString(store.getActiveStatus()));
            }
            else {
                Log.d(className, "Store Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                store = null;
            }
            cursor.close();
        }
        catch (SQLException e) {
            Log.d(className, "Exception Getting Store By id :: " + e.getMessage());
        }

        closeDB();

        return store;
    }

    public Store getStore(String storeName) {
        Store store = new Store();
        try {
            openDB();
            Log.v(className, "Querying Store by Name = " + storeName);
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, Common.colSTORE_NAME + " = '" + storeName + "'", null, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                store = cursorToStore(cursor);
                Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                        ", name = " + store.getStoreName() +
                        ", serverID = " + Integer.toString(store.getServerID()) +
                        ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                        ", activeStatus = " + Integer.toString(store.getActiveStatus()));
            }
            else {
                Log.d(className, "Store Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                store = null;
            }
            cursor.close();
        }
        catch (SQLException e) {
            Log.d(className, "Exception Getting Store By Name :: " + e.getMessage());
        }

        closeDB();

        return store;
    }

    public List<Store> getAllStores() {
        List<Store> storeList = new ArrayList<Store>();
        Store store;
        Log.v(className, "Querying List of All Stores");
        try {
            openDB();
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, null, null, null, null, Common.colSTORE_NAME);
            Log.d(className, "Number of Store Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    store = cursorToStore(cursor);
                    storeList.add(store);
                    Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                            ", name = " + store.getStoreName() +
                            ", serverID = " + Integer.toString(store.getServerID()) +
                            ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(store.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Store List Populated :: Size = " + Integer.toString(storeList.size()));
        }
        catch (SQLException e) {
            Log.e(className, "Exception Querying Store List :: " + e.getMessage());
        }
        closeDB();
        return storeList;
    }
}

