package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/7/13.
 */
public class StoreDBInterface {

    private final Context context;
    private DBHelper dbHelper;
    private String className;

    public StoreDBInterface(Context ctx) {
        className = getClass().toString();
        Log.v(className, "StoreDB(Context) Constructor");
        this.context = ctx;
        dbHelper = new DBHelper(this.context);
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
        SQLiteDatabase db;
        long insertId = -1;
        Log.d(className, "Adding Store To Database :: id = " + Integer.toString(store.getID()) +
                ", name = " + store.getStoreName() +
                ", serverID = " + Integer.toString(store.getServerID()) +
                ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                ", activeStatus = " + Integer.toString(store.getActiveStatus()));
        try {
            db = dbHelper.getWritableDatabase();
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
        catch (Exception e) {
            Log.e(className, "Exception Adding Store :: name = '" + store.getStoreName() + "' :: " + e.getMessage());
        }

        dbHelper.close();
        Log.d(className, "StoreID from Insert = " + Long.toString(insertId));
        return insertId;
    }

    public void deleteStore(Store store) {
        SQLiteDatabase db;
        Log.d(className, "Deleting Store From Database :: id = " + Integer.toString(store.getID()) +
                ", name = " + store.getStoreName() +
                ", serverID = " + Integer.toString(store.getServerID()) +
                ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                ", activeStatus = " + Integer.toString(store.getActiveStatus()));
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Performing Delete From Store Table");
            db.delete(Common.tblSTORES, Common.colSTORE_NAME + "=?", new String[] { store.getStoreName() });
        }
        catch (Exception e) {
            Log.e(className, "Exception Removing Store :: name = '" + store.getStoreName() + "' :: " + e.getMessage());
        }
        dbHelper.close();
    }

    public Store getStore(int id) {
        SQLiteDatabase db;
        Store store = new Store();
        try {
            db = dbHelper.getWritableDatabase();
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
        catch (Exception e) {
            Log.d(className, "Exception Getting Store By id :: " + e.getMessage());
        }

        dbHelper.close();

        return store;
    }

    public Store getStore(String storeName) {
        SQLiteDatabase db;
        Store store = new Store();
        try {
            db = dbHelper.getWritableDatabase();
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
        catch (Exception e) {
            Log.d(className, "Exception Getting Store By Name :: " + e.getMessage());
        }

        dbHelper.close();

        return store;
    }

    public List<Store> getAllStores() {
        SQLiteDatabase db;
        List<Store> storeList = new ArrayList<Store>();
        Store store;
        Log.v(className, "Querying List of All Stores");
        try {
            db = dbHelper.getWritableDatabase();
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
        catch (Exception e) {
            Log.e(className, "Exception Querying Store List :: " + e.getMessage());
        }
        dbHelper.close();
        return storeList;
    }

    public List<Store> getStoreUpdates() {
        SQLiteDatabase db;
        List<Store> storeList = new ArrayList<Store>();
        String whereClause = Common.colSTORE_SYNC_STATUS + " IN (?,?)";
        String[] whereArgs = {Integer.toString(Common.SYNC_STATUS_NEW), Integer.toString(Common.SYNC_STATUS_UPDATED)};

        Store store;
        Log.v(className, "Querying List of All Stores To Post to Server");
        try {
            db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, whereClause, whereArgs, null, null, Common.colSTORE_SYNC_STATUS);
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
        catch (Exception e) {
            Log.e(className, "Exception Querying Store List :: " + e.getMessage());
        }
        dbHelper.close();
        return storeList;
    }

    //Update SQLite Store Records based on List<Stores>
    public boolean updateStoreRecords(List<Store> updatedStores, int syncStatus) {
        //For each record in the List<Store>, update SQLite
        SQLiteDatabase db;
        boolean result = false;
        int updatedRecords = 0;
        String whereClause = Common.colSTORE_NAME + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Stores with Values from List");
        try {
            db = dbHelper.getWritableDatabase();

            for(Store store : updatedStores) {
                values.clear();
                values.put(Common.colSTORE_ID, store.getID());
                values.put(Common.colSTORE_NAME, store.getStoreName());
                values.put(Common.colSTORE_SERVER_ID, store.getServerID());
                values.put(Common.colSTORE_SYNC_STATUS, syncStatus);
                values.put(Common.colSTORE_ACTIVE_STATUS, store.getActiveStatus());
                whereArgs = new String[]{store.getStoreName()};
                Log.d(className, "Updating Store Record :: id = " + Integer.toString(store.getID()) +
                        ", name = " + store.getStoreName() +
                        ", serverID = " + Integer.toString(store.getServerID()) +
                        ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                        ", activeStatus = " + Integer.toString(store.getActiveStatus()));
                updatedRecords = updatedRecords + db.update(Common.tblSTORES, values, whereClause, whereArgs);
            }
            Log.d(className, "Number of Store Records = " + Integer.toString(updatedRecords));
            result = true;
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Stores from Server :: " + e.getMessage());
        }
        dbHelper.close();
        return result;
    }
}

